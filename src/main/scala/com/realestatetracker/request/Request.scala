package com.realestatetracker.request

import java.net.URI

import com.realestatetracker.entity._
import com.typesafe.scalalogging.LazyLogging
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

import scala.reflect.ClassTag


trait GetRequest[T] {
  def get: T
}

trait GetRequestBuilder[T] {
  def build: GetRequest[T]
}

trait PostRequest[T, K] {
  def post(): T
}

trait PagedPostRequest[T] extends Iterable[T] {
  def all(): List[T]
}

trait PostRequestBuilder[T, K] {
  def build: PostRequest[T, K]
}

trait PagedPostRequestBuilder[T] {
  def build: PagedPostRequest[T]
}


class RealtorUrlEncodedFormRequest(uri: URI, parameters: List[NameValuePair]) extends PagedPostRequest[RealtorResult] {
  override def all(): List[RealtorResult] = {
    iterator.toList
  }

  override def iterator: Iterator[RealtorResult] = {
    new RealtorIterator
  }

  private class RealtorIterator extends Iterator[RealtorResult] with LazyLogging{

    private var currentItems: Array[RealtorResult] = Array()
    private var currentIndex = 0
    private var nextPage = 1

    override def hasNext: Boolean = {

      def requestNextPage() = {
        logger.debug(s"Requesting page $nextPage")
        val response = HttpClient.urlEncodedPost[RealtorResponse](uri, new BasicNameValuePair("CurrentPage", nextPage.toString)::parameters)
        logger.debug(s"Got ${response.Results.length} records.")

        nextPage += 1
        currentIndex = 0
        response.Results
      }

      if (nextPage == 1 || currentIndex >= currentItems.length) {
        currentItems = requestNextPage()
        currentItems.nonEmpty
      } else {
        true
      }
    }

    override def next(): RealtorResult = {
      val item = currentItems(currentIndex)
      currentIndex += 1
      item
    }
  }
}


class EntityPostRequest[T, K](uri: URI, headers: Seq[(String, String)], body: K)(implicit ct: ClassTag[T]) extends PostRequest[T, K] {

  def post(): T = {
    HttpClient.entityPost[T, K](uri, headers, body)
  }

}

class HouseSigmaPostRequest(uri: URI, headers: Seq[(String, String)], body: HouseSigmaRequest)
  extends PostRequest[Array[HouseSigmaSoldRecord], HouseSigmaRequest] with LazyLogging {

  override def post(): Array[HouseSigmaSoldRecord] = {
    logger.debug(s"Requesting sold properties - $body")
    val postRequest = new EntityPostRequest[HouseSigmaResponse, HouseSigmaRequest](uri, headers, body)
    val response = postRequest.post()
    response.data.list
  }
}


class MongoHouseGetRequest(uri: URI, headers: Seq[(String, String)]) extends GetRequest[Array[MongoHouseResponse]] {

  override def get: Array[MongoHouseResponse] = {
    HttpClient.get[Array[MongoHouseResponse]](uri, headers)
  }
}



case class MalformedRequestException(message: String, cause: Throwable = null) extends Exception(message, cause)

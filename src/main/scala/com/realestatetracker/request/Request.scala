package com.realestatetracker.request

import java.net.URI

import com.realestatetracker.entity.{RealtorResponse, RealtorResult}
import com.typesafe.scalalogging.LazyLogging
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair


trait GetRequest[T] {
  def get: T
}

trait GetRequestBuilder[T] {
  def build: GetRequest[T]
}

trait PostRequest[T] {
  def post(): T
}

trait PagedPostRequest[T] extends Iterable[T] {
  def all(): List[T]
}

trait PostRequestBuilder[T] {
  def build: PostRequest[T]
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


case class MalformedRequestException(message: String, cause: Throwable = null) extends Exception(message, cause)

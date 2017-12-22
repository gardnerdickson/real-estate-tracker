package com.realestatetracker.request

import java.net.URI

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClientBuilder

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

object HttpClient {

  private val objectMapper = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .registerModule(DefaultScalaModule)

  private val successCodes = Set(200, 201, 202, 204)

  def urlEncodedPost[T](uri: URI, parameters: List[NameValuePair])(implicit ct: ClassTag[T]): T = {
    val httpClient = HttpClientBuilder.create.build

    val request = new HttpPost(uri)
    request.setHeader("Content-Type", "application/x-www-form-urlencoded")
    request.setEntity(new UrlEncodedFormEntity(parameters.asJava))

    val response = httpClient.execute(request)

    if (!successCodes.contains(response.getStatusLine.getStatusCode)) {
      throw HttpClientException(s"Got non-success status code: ${response.getStatusLine}")
    }

    objectMapper.readValue(response.getEntity.getContent, ct.runtimeClass).asInstanceOf[T]
  }

}


case class HttpClientException(message: String, cause: Throwable = null) extends Exception(message, cause)

package com.realestatetracker.request

import java.net.URI

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpGet, HttpPost}
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.impl.client.HttpClientBuilder

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

object HttpClient {

  private val objectMapper = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .registerModule(DefaultScalaModule)

  private val successCodes = Set(200, 201, 202, 204)

  def get[T](uri: URI, headers: Seq[(String, String)])(implicit ct: ClassTag[T]): T = {
    val httpClient = HttpClientBuilder.create.build()

    val request = new HttpGet(uri)
    for ((name, value) <- headers) {
      request.setHeader(name, value)
    }

    val response = httpClient.execute(request)

    if (!successCodes.contains(response.getStatusLine.getStatusCode)) {
      throw HttpClientException(s"Got non-success status code: ${response.getStatusLine}")
    }

    objectMapper.readValue(response.getEntity.getContent, ct.runtimeClass).asInstanceOf[T]
  }

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

  def entityPost[T, K](uri: URI, headers: Seq[(String, String)], body: K)(implicit ct: ClassTag[T]): T = {
    val httpClient = HttpClientBuilder.create.build

    val request = new HttpPost(uri)
    for ((name, value) <- headers) {
      request.setHeader(name, value)
    }

    val rawEntity = objectMapper.writeValueAsBytes(body)
    val httpEntity = new BasicHttpEntity
    httpEntity.setContent(new ByteInputStream(rawEntity, rawEntity.length))

    request.setEntity(httpEntity)

    val response = httpClient.execute(request)

    if (!successCodes.contains(response.getStatusLine.getStatusCode)) {
      throw HttpClientException(s"Got non-success status code: ${response.getStatusLine}")
    }

    objectMapper.readValue(response.getEntity.getContent, ct.runtimeClass).asInstanceOf[T]
  }

}


case class HttpClientException(message: String, cause: Throwable = null) extends Exception(message, cause)

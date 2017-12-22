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


trait GetRequest[T] {
  def get: T
}

trait GetRequestBuilder[T] {
  def build: GetRequest[T]
}

trait PostRequest[T] {
  def post(): T
}

trait PostRequestBuilder[T] {
  def build: PostRequest[T]
}


class UrlEncodedFormRequest[T](uri: URI, body: List[NameValuePair])(implicit ct: ClassTag[T]) extends PostRequest[T] {
  override def post(): T = {
    val httpClient = HttpClientBuilder.create.build()

    val request = new HttpPost(uri)
    request.setHeader("Content-Type", "application/x-www-form-urlencoded")
    request.setEntity(new UrlEncodedFormEntity(body.asJava))

    val response = httpClient.execute(request)

    val objectMapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .registerModule(DefaultScalaModule)

    val responseEntity = objectMapper.readValue(response.getEntity.getContent, ct.runtimeClass).asInstanceOf[T]

    val indented = objectMapper.writerWithDefaultPrettyPrinter.writeValueAsString(responseEntity)
    println(indented)
//    objectMapper.readValue(response.getEntity.getContent, classOf[String])

    responseEntity
  }
}


case class MalformedRequestException(message: String, cause: Throwable = null) extends Exception(message, cause)


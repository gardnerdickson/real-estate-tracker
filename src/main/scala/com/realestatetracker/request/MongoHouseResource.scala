package com.realestatetracker.request

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.realestatetracker.entity.MongoHouseResponse
import org.apache.http.client.utils.URIBuilder

import scala.collection.mutable.ListBuffer

class MongoHouseResource {

  def soldPropertyReportRequest: SoldPropertyReportRequestBuilder = {
    new SoldPropertyReportRequestBuilder
  }
}

object SoldPropertyReportRequestBuilder {
  private val URI = "https://mongohouse.com/api/reports"
}

class SoldPropertyReportRequestBuilder extends GetRequestBuilder[Array[MongoHouseResponse]] {

  private var date: Option[LocalDate] = None
  private var city: Option[String] = None

  override def build: GetRequest[Array[MongoHouseResponse]] = {
    tryValidate()

    val uri = new URIBuilder(SoldPropertyReportRequestBuilder.URI)
      .setParameter("date", DateTimeFormatter.ofPattern("MM/dd/yyyy").format(date.get))
      .setParameter("city", city.get)
      .build()

    new MongoHouseGetRequest(uri, Seq())
  }

  def date(date: LocalDate): SoldPropertyReportRequestBuilder = {
    this.date = Some(date)
    this
  }

  def city(city: String): SoldPropertyReportRequestBuilder = {
    this.city = Some(city)
    this
  }

  private def tryValidate(): Unit = {
    val missingFields = new ListBuffer[String]
    if (date.isEmpty) {
      missingFields.append("date")
    }
    if (city.isEmpty) {
      missingFields.append("city")
    }
    if (missingFields.nonEmpty) {
      throw MalformedRequestException(s"There are missing parameters: ${missingFields.mkString(", ")}")
    }
  }
}

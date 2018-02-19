package com.realestatetracker.request

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}

import com.realestatetracker.config.Config
import com.realestatetracker.entity.MongoHouseResponse
import org.apache.http.client.utils.URIBuilder

import scala.collection.mutable.ListBuffer

class MongoHouseResource {

  def soldPropertyReportRequest: SoldPropertyReportRequestBuilder = {
    new SoldPropertyReportRequestBuilder
  }
}

object SoldPropertyReportRequestBuilder {
  private val dateTimePatternSpec = "{MONTH}/{DAY}/{YEAR}"
}

class SoldPropertyReportRequestBuilder extends GetRequestBuilder[Array[MongoHouseResponse]] {

  private var date: Option[LocalDate] = None
  private var city: Option[String] = None

  override def build: GetRequest[Array[MongoHouseResponse]] = {
    tryValidate()

    val monthPattern = if (date.get.getMonthValue < 10) "M" else "MM"
    val dayPattern  = if (date.get.getDayOfMonth < 10) "d" else "dd"
    val dateTimePattern = SoldPropertyReportRequestBuilder.dateTimePatternSpec
      .replace("{MONTH}", monthPattern)
      .replace("{DAY}", dayPattern)
      .replace("{YEAR}", "yyyy")

    val uri = new URIBuilder(Config.mongoHouseRequestUri)
      .setParameter("date", DateTimeFormatter.ofPattern(dateTimePattern).format(date.get))
      .setParameter("city", city.get)
      .build()

    new MongoHouseGetRequest(uri)
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

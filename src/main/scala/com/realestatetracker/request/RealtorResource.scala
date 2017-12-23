package com.realestatetracker.request

import com.realestatetracker.entity.RealtorResult
import com.typesafe.scalalogging.LazyLogging
import org.apache.http.client.utils.URIBuilder
import org.apache.http.message.BasicNameValuePair

import scala.collection.mutable.ListBuffer

class RealtorResource {
  def propertiesRequest: RealtorPropertiesRequestBuilder = {
    new RealtorPropertiesRequestBuilder
  }
}


object RealtorPropertiesRequestBuilder {
  private val URI = new URIBuilder("https://api2.realtor.ca")
    .setPath("/Listing.svc/PropertySearch_Post")
    .build()
}

class RealtorPropertiesRequestBuilder extends PagedPostRequestBuilder[RealtorResult] with LazyLogging {

  private var minimumPrice: Option[Int] = None
  private var maximumPrice: Option[Int] = None
  private var minimumLongitude: Option[Float] = None
  private var maximumLongitude: Option[Float] = None
  private var minimumLatitude: Option[Float] = None
  private var maximumLatitude: Option[Float] = None

  override def build: PagedPostRequest[RealtorResult] = {

    tryValidate()

    val formParameters = List(
      new BasicNameValuePair("PriceMin", minimumPrice.get.toString),
      new BasicNameValuePair("PriceMax", maximumPrice.get.toString),
      new BasicNameValuePair("LongitudeMin", minimumLongitude.get.toString),
      new BasicNameValuePair("LongitudeMax", maximumLongitude.get.toString),
      new BasicNameValuePair("LatitudeMin", minimumLatitude.get.toString),
      new BasicNameValuePair("LatitudeMax", maximumLatitude.get.toString),

      // Non-user specified parameters
      new BasicNameValuePair("CultureId", "1"),
      new BasicNameValuePair("ApplicationId", "1"),
      new BasicNameValuePair("RecordsPerPage", "50"),
      new BasicNameValuePair("PropertySearchTypeId", "1"), // Residential
      new BasicNameValuePair("TransactionTypeId", "2"), // For sale
      new BasicNameValuePair("Version", "6.0")
    )

    logger.info("Sending request with form parameters: ")
    formParameters.foreach(param => logger.info(s"\t${param.getName}: ${param.getValue}"))

    new RealtorUrlEncodedFormRequest(RealtorPropertiesRequestBuilder.URI, formParameters)
  }

  def tryValidate(): Unit = {
    val missingFields = new ListBuffer[String]
    if (minimumPrice.isEmpty) {
      missingFields.append("minimumPrice")
    }
    if (maximumPrice.isEmpty) {
      missingFields.append("maximumPrice")
    }
    if (minimumLongitude.isEmpty) {
      missingFields.append("minimumLongitude")
    }
    if (maximumLongitude.isEmpty) {
      missingFields.append("maximumLongitude")
    }
    if (minimumLatitude.isEmpty) {
      missingFields.append("minimumLatitude")
    }
    if (maximumLatitude.isEmpty) {
      missingFields.append("maximumLatitude")
    }

    if (missingFields.nonEmpty) {
      throw MalformedRequestException(s"There are missing parameters: ${missingFields.mkString(", ")}")
    }
  }

  def minimumPrice(price: Int): RealtorPropertiesRequestBuilder = {
    this.minimumPrice = Some(price)
    this
  }

  def maximumPrice(price: Int): RealtorPropertiesRequestBuilder = {
    this.maximumPrice = Some(price)
    this
  }

  def minimumLongitude(longitude: Float): RealtorPropertiesRequestBuilder = {
    this.minimumLongitude = Some(longitude)
    this
  }

  def maximumLongitude(longitude: Float): RealtorPropertiesRequestBuilder = {
    this.maximumLongitude = Some(longitude)
    this
  }

  def minimumLatitude(latitude: Float): RealtorPropertiesRequestBuilder = {
    this.minimumLatitude = Some(latitude)
    this
  }

  def maximumLatitude(latitude: Float): RealtorPropertiesRequestBuilder = {
    this.maximumLatitude = Some(latitude)
    this
  }

}



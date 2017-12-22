package com.realestatetracker.request

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

class RealtorPropertiesRequestBuilder extends PostRequestBuilder[RealtorResponse] {

  private var minimumPrice: Option[Int] = None
  private var maximumPrice: Option[Int] = None
  private var minimumLongitude: Option[Float] = None
  private var maximumLongitude: Option[Float] = None
  private var minimumLatitude: Option[Float] = None
  private var maximumLatitude: Option[Float] = None

  override def build: PostRequest[RealtorResponse] = {

    tryValidate()

    val formParameters = List(
      new BasicNameValuePair("MinimumPrice", minimumPrice.get.toString),
      new BasicNameValuePair("MaximumPrice", maximumPrice.get.toString),
      new BasicNameValuePair("MinimumLongitude", minimumLongitude.get.toString),
      new BasicNameValuePair("MaximumLongitude", maximumLongitude.get.toString),
      new BasicNameValuePair("MinimumLatitude", minimumLatitude.get.toString),
      new BasicNameValuePair("MaximumLatitude", maximumLatitude.get.toString),

      // Non-user specified parameters
      new BasicNameValuePair("CultureId", "1"),
      new BasicNameValuePair("ApplicationId", "1"),
      new BasicNameValuePair("RecordsPerPage", "50"),
      new BasicNameValuePair("PropertySearchTypeId", "1"), // Residential
      new BasicNameValuePair("Version", "6.0")
    )



    new UrlEncodedFormRequest[RealtorResponse](RealtorPropertiesRequestBuilder.URI, formParameters)
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



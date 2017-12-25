package com.realestatetracker.request

import com.realestatetracker.config.Config
import com.realestatetracker.entity.{HouseSigmaRequest, HouseSigmaResponse, HouseSigmaSoldRecord}
import com.typesafe.scalalogging.LazyLogging
import org.apache.http.client.utils.URIBuilder

import scala.collection.mutable.ListBuffer

class HouseSigmaResource {

  def soldPropertiesRequest: SoldPropertiesRequestBuilder = {
    new SoldPropertiesRequestBuilder
  }
}

object SoldPropertiesRequestBuilder {
  private val URI = new URIBuilder(Config.houseSigmaRequestUri).build()
}

class SoldPropertiesRequestBuilder extends PostRequestBuilder[Array[HouseSigmaSoldRecord], HouseSigmaRequest] with LazyLogging {

  private var houseType: Option[HouseSigmaHouseType] = None
  private var daysSinceSale: Option[Int] = None
  private var minimumPrice: Option[Int] = None
  private var maximumPrice: Option[Int] = None
  private var minimumNumberOfBathrooms: Option[Int] = None
  private var minimumNumberOfGarages: Option[Int] = None
  private var minimumNumberOfBedrooms: Option[Int] = None
  private var maximumNumberOfBedrooms: Option[Int] = None
  private var minimumLongitude: Option[Float] = None
  private var maximumLongitude: Option[Float] = None
  private var minimumLatitude: Option[Float] = None
  private var maximumLatitude: Option[Float] = None

  override def build: PostRequest[Array[HouseSigmaSoldRecord], HouseSigmaRequest] = {

    def resolveBedroomRange = {
      if (minimumNumberOfBedrooms.isDefined && maximumNumberOfBedrooms.isDefined) {
        Array(minimumNumberOfBedrooms.get, maximumNumberOfBedrooms.get)
      } else {
        Array(0)
      }
    }

    tryValidate()

    val requestBody = HouseSigmaRequest(
      "en_US",
      houseType.get.typeCode(),
      daysSinceSale.get,
      resolveBedroomRange,
      minimumPrice.get,
      maximumPrice.get,
      minimumNumberOfBathrooms.getOrElse(0),
      minimumNumberOfGarages.getOrElse(0),
      minimumLatitude.get,
      maximumLatitude.get,
      minimumLatitude.get,
      maximumLatitude.get
    )

    val headers = Seq(("Authorization", "Bearer o1us79gc2grbqnjqcgos0jhi4r"))

    new HouseSigmaPostRequest(SoldPropertiesRequestBuilder.URI, headers, requestBody)
  }

  def houseType(houseType: HouseSigmaHouseType): SoldPropertiesRequestBuilder = {
    this.houseType = Some(houseType)
    this
  }

  def daysSinceSale(days: Int): SoldPropertiesRequestBuilder = {
    this.daysSinceSale = Some(days)
    this
  }

  def minimumPrice(price: Int): SoldPropertiesRequestBuilder = {
    this.minimumPrice = Some(price)
    this
  }

  def maximumPrice(price: Int): SoldPropertiesRequestBuilder = {
    this.maximumPrice = Some(price)
    this
  }

  def minimumNumberOfBedrooms(number: Int): SoldPropertiesRequestBuilder = {
    this.minimumNumberOfBedrooms = Some(number)
    this
  }

  def maximumNumberOfBedrooms(number: Int): SoldPropertiesRequestBuilder = {
    this.maximumNumberOfBedrooms = Some(number)
    this
  }

  def minimumNumberOfBathrooms(number: Int): SoldPropertiesRequestBuilder = {
    this.minimumNumberOfBathrooms = Some(number)
    this
  }

  def minimumNumberOfGarages(number: Int): SoldPropertiesRequestBuilder = {
    this.minimumNumberOfGarages = Some(number)
    this
  }

  def minimumLongitude(longitude: Float): SoldPropertiesRequestBuilder = {
    this.minimumLongitude = Some(longitude)
    this
  }

  def maximumLongitude(longitude: Float): SoldPropertiesRequestBuilder = {
    this.maximumLongitude = Some(longitude)
    this
  }

  def minimumLatitude(latitude: Float): SoldPropertiesRequestBuilder = {
    this.minimumLatitude = Some(latitude)
    this
  }

  def maximumLatitude(latitude: Float): SoldPropertiesRequestBuilder = {
    this.maximumLatitude = Some(latitude)
    this
  }

  private def tryValidate(): Unit = {
    val missingFields = new ListBuffer[String]
    if (houseType.isEmpty) {
      missingFields.append("houseType")
    }
    if (daysSinceSale.isEmpty) {
      missingFields.append("daysSinceSale")
    }
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

}

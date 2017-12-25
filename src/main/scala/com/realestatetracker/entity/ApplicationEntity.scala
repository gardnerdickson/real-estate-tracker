package com.realestatetracker.entity

import java.sql.ResultSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.realestatetracker.config.Config

import scala.util.Try


trait ApplicationEntity {

}

case class PropertyListing(
  realtorId: Long,
  mlsNumber: String,
  description: String,
  numberOfBathrooms: String,
  numberOfBedrooms: String,
  buildingType: String,
  price: Int,
  address: String,
  longitude: Float,
  latitude: Float,
  postalCode: String,
) extends ApplicationEntity

object PropertyListing {

  def apply(realtorResult: RealtorResult): PropertyListing = {

    def normalizeAndConvertPrice(price: String): Int = {
      price
        .replace("$", "")
        .replace(",", "")
        .replace("+ HST", "")
        .replace("+ GST", "")
        .replace(" ", "")
        .toInt
    }

    def normalizeDescription(description: String): String = {
      description.replace("'", "")
    }

    new PropertyListing(
      realtorResult.id,
      realtorResult.mlsNumber,
      normalizeDescription(realtorResult.publicRemarks),
      realtorResult.building.bathroomTotal,
      realtorResult.building.bedrooms,
      realtorResult.building.buildingType,
      normalizeAndConvertPrice(realtorResult.property.price),
      realtorResult.property.address.addressText,
      realtorResult.property.address.longitude,
      realtorResult.property.address.latitude,
      realtorResult.postalCode
    )
  }

  def apply(realtorResults: List[RealtorResult]): List[PropertyListing] = {
    realtorResults.map(PropertyListing.apply)
  }
}

case class PriceChangePropertyListing(
  mlsNumber: String,
  oldPrice: Int,
  newPrice: Int
) extends ApplicationEntity


case class SigmaSoldProperty(
  sigmaId: String,
  mlsNumber: String,
  latitude: Float,
  longitude: Float,
  listedPrice: String,
  soldPrice: Int
) extends ApplicationEntity

object SigmaSoldProperty {

  def apply(houseSigmaSoldRecord: HouseSigmaSoldRecord): SigmaSoldProperty = {
    new SigmaSoldProperty(
      houseSigmaSoldRecord.hashId,
      houseSigmaSoldRecord.mlsNumber,
      houseSigmaSoldRecord.lat,
      houseSigmaSoldRecord.lng,
      houseSigmaSoldRecord.price,
      houseSigmaSoldRecord.sold
    )
  }

  def apply(houseSigmaSoldRecords: Array[HouseSigmaSoldRecord]): Array[SigmaSoldProperty] = {
    houseSigmaSoldRecords.map(SigmaSoldProperty.apply)
  }

}


case class MongoSoldProperty(
  mongoId: String,
  mlsNumber: String,
  daysOnMarket: Int,
  dateListed: LocalDate,
  listedPrice: Int,
  dateSold: LocalDate,
  soldPrice: Int
) extends ApplicationEntity

object MongoSoldProperty {

  private def resolveDate(date: String): LocalDate = {
    val dateFormats = Config.mongoHouseResponseDateFormats
    var localDate: LocalDate = null
    var index = 0
    while (localDate == null) {
      localDate = Try(LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormats(index)))).getOrElse(null)
      index += 1
      if (index > 3) {
        throw new IndexOutOfBoundsException(s"Failed to parse date: $date. The format doesn't match any of ${dateFormats.mkString(", ")}")
      }
    }
    localDate
  }

  def apply(mongoHouseResponse: MongoHouseResponse): MongoSoldProperty = {
    new MongoSoldProperty(
      mongoHouseResponse.mongoId,
      mongoHouseResponse.mlsNumber,
      mongoHouseResponse.dom,
      resolveDate(mongoHouseResponse.contractDate),
      mongoHouseResponse.listedPrice,
      resolveDate(mongoHouseResponse.soldDate),
      mongoHouseResponse.soldPrice
    )
  }

  def apply(mongoHouseResponseRecords: Array[MongoHouseResponse]): Array[MongoSoldProperty] = {
    mongoHouseResponseRecords.map(MongoSoldProperty.apply)
  }

}

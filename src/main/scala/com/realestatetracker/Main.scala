package com.realestatetracker

import java.time.LocalDate

import com.realestatetracker.request.{HouseSigmaHouseType, HouseSigmaResource, MongoHouseResource, RealtorResource}
import com.typesafe.scalalogging.LazyLogging


object Main extends LazyLogging {

  private val torontoMinLongitude = -79.43675788630219f
  private val torontoMaxLongitude =  -79.34079917658539f
  private val torontoMinLatitude = 43.62966297767042f
  private val torontoMaxLatitude = 43.68188782800465f
  private val minPrice = 400000
  private val maxPrice = 900000

  def main(args: Array[String]): Unit = {

    val realtorRequest = new RealtorResource()
      .propertiesRequest
      .minimumLongitude(torontoMinLongitude)
      .maximumLongitude(torontoMaxLongitude)
      .minimumLatitude(torontoMinLatitude)
      .maximumLatitude(torontoMaxLatitude)
      .minimumPrice(minPrice)
      .maximumPrice(maxPrice)
      .build

//    val realtorResults = realtorRequest.all()
//    logger.info(s"Found ${realtorResults.length} property listings.")
//    val propertyListings = PropertyListing(realtorResults)
//
//    logger.info("Trying add listings to database.")
//    val propertyListingRepo = new PropertyListingRepository
//    propertyListingRepo.insertPropertyListings(propertyListings)

    val houseSigmaRequest = new HouseSigmaResource()
      .soldPropertiesRequest
      .daysSinceSale(90)
      .houseType(HouseSigmaHouseType.CONDO_APT)
      .minimumLongitude(torontoMinLongitude)
      .maximumLongitude(torontoMaxLongitude)
      .minimumLatitude(torontoMinLatitude)
      .maximumLatitude(torontoMaxLatitude)
      .minimumPrice(minPrice)
      .maximumPrice(maxPrice)
      .build

//    val houseSigmaResult = houseSigmaRequest.post()
//    logger.info("Got sold properties results: " + houseSigmaResult.length)
//    houseSigmaResult.foreach(x => println(x.ml_num))

    val mongoHouseRequest = new MongoHouseResource()
      .soldPropertyReportRequest
      .date(LocalDate.of(2017, 12, 21))
      .city("Toronto")
      .build

    val mongoHouseResponse = mongoHouseRequest.get


    println

  }
}

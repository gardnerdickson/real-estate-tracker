package com.realestatetracker.main

import java.time.LocalDate

import com.realestatetracker.config.Config
import com.realestatetracker.entity.{MongoSoldProperty, PropertyListing, SigmaSoldProperty}
import com.realestatetracker.repository.{MongoSoldPropertyRepository, PropertyListingRepository, SigmaSoldPropertyRepository}
import com.realestatetracker.request._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable.ListBuffer


object PropertyDownloader extends LazyLogging {

  def getProcess: Process = {
    new Process(ProcessType.DOWNLOAD_PROPERTIES, (executionId: Long, date: LocalDate) => {
      downloadRealtorProperties(executionId)
      downloadMongoHouseProperties(executionId, date)
    })
  }


  private def downloadRealtorProperties(executionId: Long): Unit = {
    val realtorRequest = new RealtorResource()
      .propertiesRequest
      .minimumLongitude(Config.minimumLongitude)
      .maximumLongitude(Config.maximumLongitude)
      .minimumLatitude(Config.minimumLatitude)
      .maximumLatitude(Config.maximumLatitude)
      .minimumPrice(Config.minimumPrice)
      .maximumPrice(Config.maximumPrice)
      .build

    val realtorResults = realtorRequest.post()
    logger.info(s"Found ${realtorResults.length} property listings.")
    val propertyListings = PropertyListing(executionId, realtorResults)

    // DEBUG
    //    propertyListings.foreach(println)

    logger.info("Adding realtor listings to the database.")
    val propertyListingRepo = new PropertyListingRepository
    propertyListingRepo.insertPropertyListings(propertyListings)
    logger.info("Done adding realtor listings to the database.")
  }

  private def downloadMongoHouseProperties(executionId: Long, date: LocalDate): Unit = {
    val soldPropertyRecords = new ListBuffer[MongoSoldProperty]
    for (offset <- 1 to Config.mongoHouseDateRange) {
      val reportDate = date.minusDays(offset)
      val mongoHouseRequest = new MongoHouseResource()
        .soldPropertyReportRequest
        .date(reportDate)
        .city("Toronto")
        .build

      val mongoHouseRecords = mongoHouseRequest.get
      logger.info(s"Got sold properties from mongohouse.com. ${mongoHouseRecords.length} total records.")
      logger.info(s"Filtering for price range ${Config.minimumPrice} to ${Config.maximumPrice}")
      val priceFilteredRecords = mongoHouseRecords.filter(property => {
        property.listedPrice >= Config.minimumPrice && property.listedPrice <= Config.maximumPrice
      })
      logger.info(s"Filtered out ${mongoHouseRecords.length - priceFilteredRecords.length} records. ${priceFilteredRecords.length} records left.")
      soldPropertyRecords.append(MongoSoldProperty(executionId, priceFilteredRecords): _*)
    }

    // DEBUG
//    soldPropertyRecords.foreach(println)

    logger.info("Adding mongohouse properties to the database.")
    val mongoRepository = new MongoSoldPropertyRepository
    mongoRepository.insertSoldProperties(soldPropertyRecords)
    logger.info("Done adding mongohouse properties to the database.")
  }

  private def downloadSigmaHouseProperties(executionId: Long): Unit = {
    val houseSigmaRequest = new HouseSigmaResource()
      .soldPropertiesRequest
      .daysSinceSale(90)
      .houseType(HouseSigmaHouseType.CONDO_APT)
      .minimumLongitude(Config.minimumLongitude)
      .maximumLongitude(Config.maximumLongitude)
      .minimumLatitude(Config.minimumLatitude)
      .maximumLatitude(Config.maximumLatitude)
      .minimumPrice(Config.minimumPrice)
      .maximumPrice(Config.maximumPrice)
      .build

    val houseSigmaResult = houseSigmaRequest.post()
    logger.info("Got sold properties results from housesigma.com: " + houseSigmaResult.length)
    val houseSigmaProperties = SigmaSoldProperty(executionId, houseSigmaResult)

    // DEBUG
    //    houseSigmaProperties.foreach(println)

    logger.info("Adding housesigma properties to the database.")
    val sigmaRepository = new SigmaSoldPropertyRepository
    sigmaRepository.insertSoldProperties(houseSigmaProperties)
    logger.info("Done adding housesigma properties to the database.")
  }

}

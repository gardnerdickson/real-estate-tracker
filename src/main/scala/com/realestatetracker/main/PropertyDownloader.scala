package com.realestatetracker.main

import java.time.LocalDate

import com.realestatetracker.config.Config
import com.realestatetracker.entity.{MongoSoldProperty, PropertyListing, SigmaSoldProperty}
import com.realestatetracker.repository.{ExecutionRepository, MongoSoldPropertyRepository, PropertyListingRepository, SigmaSoldPropertyRepository}
import com.realestatetracker.request.{HouseSigmaHouseType, HouseSigmaResource, MongoHouseResource, RealtorResource}
import com.typesafe.scalalogging.LazyLogging


object PropertyDownloader extends LazyLogging {

  def main(args: Array[String]): Unit = {

    val date = LocalDate.parse(args(0), Config.commandLineDateFormat)
    val executionRepository = new ExecutionRepository()
    val executionId = executionRepository.createExecutionLog(date)

    try {
      downloadRealtorProperties(executionId)
      downloadMongoHouseProperties(executionId, LocalDate.parse(args(0), Config.commandLineDateFormat))
      //      downloadSigmaHouseProperties(executionId)
      executionRepository.updateExecution(executionId, "COMPLETE")
    } catch {
      case e: Exception =>
        logger.error("Unhandled exception caught. Setting execution to 'FAILED'")
        executionRepository.updateExecution(executionId, "FAILED")
        throw e
    }
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

  private def downloadMongoHouseProperties(executionId: Long, reportDate: LocalDate): Unit = {
    val mongoHouseRequest = new MongoHouseResource()
      .soldPropertyReportRequest
      .date(reportDate)
      .city("Toronto")
      .build

    val mongoHouseRecords = mongoHouseRequest.get
    logger.info(s"Got sold properties from mongohouse.com. ${mongoHouseRecords.length} total records.")
    val mongoSoldProperties = MongoSoldProperty(executionId, mongoHouseRecords)

    // DEBUG
    //    mongoSoldProperties.foreach(println)

    logger.info("Adding mongohouse properties to the database.")
    val mongoRepository = new MongoSoldPropertyRepository
    mongoRepository.insertSoldProperties(mongoSoldProperties)
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

package com.realestatetracker.config

import java.time.format.DateTimeFormatter

object Config {

  val commandLineDateFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  val databaseDriver = "org.h2.Driver"
  val databaseConnection = "jdbc:h2:tcp://localhost/~/test"
  val databaseUsername = "sa"
  val databasePassword = ""

  val minimumLatitude: Float = 43.62966297767042f
  val maximumLatitude: Float = 43.68188782800465f
  val minimumLongitude: Float = -79.43675788630219f
  val maximumLongitude: Float = -79.34079917658539f
  val minimumPrice: Int = 400000
  val maximumPrice: Int = 900000

  val realtorRequestUri = "http://api2.realtor.ca/Listing.svc/PropertySearch_Post"

  val mongoHouseRequestUri = "http://mongohouse.com/api/reports"
  val mongoHouseRequestDateFormat: String = "MM/dd/yyyy"
  val mongoHouseResponseDateFormats: Array[String] = Array("MM/dd/yyyy", "MM/d/yyyy", "M/d/yyyy", "M/dd/yyyy")

  val houseSigmaRequestUri = "http://housesigma.com/bkv2/api/search/mapsearch/sold"
}

package com.realestatetracker.config

import java.io.FileNotFoundException
import java.nio.file.{Files, Paths}
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Properties

object Config {

  private val settings = {
    val configFile = System.getProperty("config.file")
    if (configFile == null) {
      throw new IllegalArgumentException("JVM argument 'config.file' not set.")
    }
    val path = Paths.get(configFile)
    if (Files.notExists(path)) {
      throw new FileNotFoundException(s"Failed to load file: $path")
    }
    val props = new Properties()
    props.load(Files.newInputStream(path))
    props
  }

  def databaseDriver: String = settings.getProperty("database.driver")
  def databaseConnection: String = settings.getProperty("database.connection")
  def databaseUsername: String = settings.getProperty("database.username")
  def databasePassword: String = settings.getProperty("database.password")

  def minimumLatitude: Float = settings.getProperty("location.latitude.min").toFloat
  def maximumLatitude: Float = settings.getProperty("location.latitude.max").toFloat
  def minimumLongitude: Float = settings.getProperty("location.longitude.min").toFloat
  def maximumLongitude: Float = settings.getProperty("location.longitude.max").toFloat
  def minimumPrice: Int = settings.getProperty("price.min").toInt
  def maximumPrice: Int = settings.getProperty("price.max").toInt

  def realtorRequestUri: String = settings.getProperty("api.realtor.uri")
  def mongoHouseRequestUri: String = settings.getProperty("api.mongohouse.uri")
  def houseSigmaRequestUri: String = settings.getProperty("api.housesigma.uri")

  val commandLineDateFormat: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
  val mongoHouseRequestDateFormat: String = "MM/dd/yyyy"
  val mongoHouseResponseDateFormats: Array[String] = Array("MM/dd/yyyy", "MM/d/yyyy", "M/d/yyyy", "M/dd/yyyy")

  def reportDirectory: String = settings.getProperty("report.directory")
  def reportFile(date: LocalDate): String = {
    settings.getProperty("report.filePattern").replace("${DATE}", date.toString)
  }

  def emailFromAddress: String = settings.getProperty("email.fromAddress")
  def emailRecipients: Array[String] = settings.getProperty("email.toAddresses").split(",")
  def emailUsername: String = settings.getProperty("email.username")
  def emailPassword: String = settings.getProperty("email.password")

}

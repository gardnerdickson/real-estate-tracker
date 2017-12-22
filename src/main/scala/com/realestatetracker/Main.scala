package com.realestatetracker

import com.realestatetracker.request.{RealtorProperty, RealtorResource}
import com.typesafe.scalalogging.LazyLogging


object Main extends LazyLogging {

  private val torontoMinLongitude = -79.43675788630219f
  private val torontoMaxLongitude =  -79.34079917658539f
  private val torontoMinLatitude = 43.62966297767042f
  private val torontoMaxLatitude = 43.68188782800465f
  private val minPrice = 400000
  private val maxPrice = 900000

  def main(args: Array[String]): Unit = {

    val request = new RealtorResource()
      .propertiesRequest
      .minimumLongitude(torontoMinLongitude)
      .maximumLongitude(torontoMaxLongitude)
      .minimumLatitude(torontoMinLatitude)
      .maximumLatitude(torontoMaxLatitude)
      .minimumPrice(minPrice)
      .maximumPrice(maxPrice)
      .build

    val realtorResults = request.all()
    val properties = RealtorProperty(realtorResults)

    properties.foreach(x => println(x.price))

  }
}

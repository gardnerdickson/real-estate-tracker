package com.realestatetracker

import com.realestatetracker.request.{RealtorProperty, RealtorResource}


object Main {

  private val torontoMinLongitude = -79.443024f
  private val torontoMaxLongitude =  -79.342944f
  private val torontoMinLatitude = 43.633278f
  private val torontoMaxLatitude = 43.677901f
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

    val realtorResponse = request.post()
    println("Number of properties: " + realtorResponse.Paging.TotalRecords)



//    val realtorProperty = RealtorProperty(realtorResponse.Results(0))
//    println(realtorProperty)
  }
}

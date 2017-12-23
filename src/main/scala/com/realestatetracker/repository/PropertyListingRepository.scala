package com.realestatetracker.repository

import java.sql.{Connection, DriverManager}

import com.realestatetracker.entity.PropertyListing

class PropertyListingRepository {

  Class.forName("org.h2.Driver")

  def insertPropertyListings(propertyListings: Seq[PropertyListing]): Unit = {
    val connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "")

    val statements = propertyListings.map(listing => connection.prepareStatement(insertStatement(listing)))
    statements.foreach(_.execute())

    connection.close()
  }

  private def insertStatement(propertyListing: PropertyListing): String = {
    s"""
      |insert into property_listing
      |(
      |  realtor_id,
      |  mls_number,
      |  description,
      |  num_bathrooms,
      |  num_bedrooms,
      |  building_type,
      |  price,
      |  address,
      |  longitude,
      |  latitude,
      |  postal_code
      |)
      |values
      |(
      |  ${propertyListing.realtorId},
      |  ${propertyListing.mlsNumber},
      |  ${propertyListing.description},
      |  ${propertyListing.numberOfBathrooms},
      |  ${propertyListing.numberOfBedrooms},
      |  ${propertyListing.buildingType},
      |  ${propertyListing.price},
      |  ${propertyListing.address},
      |  ${propertyListing.longitude},
      |  ${propertyListing.latitude},
      |  ${propertyListing.postalCode}
      |)
    """.stripMargin
  }
}

package com.realestatetracker.repository

import java.sql.DriverManager

import com.realestatetracker.config.Config
import com.realestatetracker.entity.PropertyListing


object PropertyListingRepository {
  private val insertStatement =
    """
      |insert into realtor_property_listing
      |(realtor_id, mls_number, description, num_bathrooms, num_bedrooms, building_type, price, address, longitude, latitude, postal_code)
      |values
      |(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.stripMargin
}

class PropertyListingRepository {

  Class.forName(Config.databaseDriver)

  def insertPropertyListings(propertyListings: Seq[PropertyListing]): Unit = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    connection.setAutoCommit(false)

    val preparedStatement = connection.prepareStatement(PropertyListingRepository.insertStatement)
    for (propertyListing <- propertyListings) {
      preparedStatement.setLong(1, propertyListing.realtorId)
      preparedStatement.setString(2, propertyListing.mlsNumber)
      preparedStatement.setString(3, propertyListing.description)
      preparedStatement.setString(4, propertyListing.numberOfBathrooms)
      preparedStatement.setString(5, propertyListing.numberOfBedrooms)
      preparedStatement.setString(6, propertyListing.buildingType)
      preparedStatement.setInt(7, propertyListing.price)
      preparedStatement.setString(8, propertyListing.address)
      preparedStatement.setFloat(9, propertyListing.longitude)
      preparedStatement.setFloat(10, propertyListing.latitude)
      preparedStatement.setString(11, propertyListing.postalCode)
      preparedStatement.addBatch()
    }

    preparedStatement.executeBatch()
    preparedStatement.clearBatch()
    connection.commit()
    connection.close()
  }


}

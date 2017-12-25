package com.realestatetracker.repository

import java.sql.{DriverManager, ResultSet}
import java.time.LocalDate

import com.realestatetracker.config.Config
import com.realestatetracker.entity.{PriceChangePropertyListing, PropertyListing}

import scala.collection.mutable.ListBuffer


object PropertyListingRepository {
  private val insertStatement =
    """
      |insert into realtor_property_listing
      |(realtor_id, mls_number, description, num_bathrooms, num_bedrooms, building_type, price, address, longitude, latitude, postal_code)
      |values
      |(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.stripMargin

  private val queryByTimestampStatement =
    """
      |select * from realtor_property_listing
      |where au_created_timestamp like ?
    """.stripMargin

  private val changedPriceStatement =
    """
      |select old.mls_number, old.price, new.price from realtor_property_listing old
      |inner join realtor_property_listing new
      |on old.mls_number = new.mls_number
      |where 1=1
      |and old.au_created_timestamp like ?
      |and new.au_created_timestamp like ?
      |and old.price != new.price
    """.stripMargin

  private def convertResultSet(resultSet: ResultSet): List[PropertyListing] = {
    val listBuffer = new ListBuffer[PropertyListing]
    while (resultSet.next()) {
      listBuffer.append(PropertyListing(
        resultSet.getLong("realtor_id"),
        resultSet.getString("mls_number"),
        resultSet.getString("description"),
        resultSet.getString("num_bathrooms"),
        resultSet.getString("num_bedrooms"),
        resultSet.getString("building_type"),
        resultSet.getInt("price"),
        resultSet.getString("address"),
        resultSet.getFloat("longitude"),
        resultSet.getFloat("latitude"),
        resultSet.getString("postal_code")
      ))
    }
    listBuffer.toList
  }

  private def convertResultSetToPriceChangedProperty(resultSet: ResultSet): List[PriceChangePropertyListing] = {
    val listBuffer = new ListBuffer[PriceChangePropertyListing]
    while (resultSet.next()) {
      listBuffer.append(PriceChangePropertyListing(resultSet.getString("old.mls_number"), resultSet.getInt("old.price"), resultSet.getInt("new.price")))
    }
    listBuffer.toList
  }

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

  def queryByDateCreated(date: LocalDate): List[PropertyListing] = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(PropertyListingRepository.queryByTimestampStatement)
    preparedStatement.setString(1, s"$date%")
    val resultSet = preparedStatement.executeQuery()
    val propertyListings = PropertyListingRepository.convertResultSet(resultSet)
    connection.close()
    propertyListings
  }

  def queryForChangedPrices(oldDate: LocalDate, newDate: LocalDate): List[PriceChangePropertyListing] = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(PropertyListingRepository.changedPriceStatement)
    preparedStatement.setString(1, s"$oldDate%")
    preparedStatement.setString(2, s"$newDate%")
    val resultSet = preparedStatement.executeQuery()
    val changedProperties = PropertyListingRepository.convertResultSetToPriceChangedProperty(resultSet)
    connection.close()
    changedProperties
  }

}

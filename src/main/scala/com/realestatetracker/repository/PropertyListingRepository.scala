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
      |(au_execution_id, realtor_id, mls_number, description, num_bathrooms, num_bedrooms, building_type, price, address, longitude, latitude, postal_code)
      |values
      |(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.stripMargin

  private val queryByTimestampStatement =
    """
      |select * from realtor_property_listing
      |where au_created_timestamp like ?
    """.stripMargin

  private val queryByExecutionId =
    """
      |select * from realtor_property_listing
      |where au_execution_id = ?
    """.stripMargin

  private val changedPriceStatement =
    """
      |select new.*, old.price as old_price, new.price as new_price
      |from realtor_property_listing old
      |inner join realtor_property_listing new
      |on old.mls_number = new.mls_number
      |where 1=1
      |and old.au_execution_id = ?
      |and new.au_execution_id = ?
      |and old.price != new.price
    """.stripMargin

  private val newPropertiesStatement =
    """
      |select * from realtor_property_listing new
      |where au_execution_id = ?
      |and mls_number not in (
      | select mls_number from realtor_property_listing
      | where au_execution_id = ?
      |)
    """.stripMargin

  private def convertResultSet(resultSet: ResultSet): List[PropertyListing] = {
    val listBuffer = new ListBuffer[PropertyListing]
    while (resultSet.next()) {
      listBuffer.append(PropertyListing(
        resultSet.getLong("au_execution_id"),
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
      listBuffer.append(
        PriceChangePropertyListing(
          PropertyListing(
            resultSet.getLong("au_execution_id"),
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
          ),
          resultSet.getInt("old_price"),
          resultSet.getInt("new_price")
        )
      )
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
      preparedStatement.setLong(1, propertyListing.executionId)
      preparedStatement.setLong(2, propertyListing.realtorId)
      preparedStatement.setString(3, propertyListing.mlsNumber)
      preparedStatement.setString(4, propertyListing.description)
      preparedStatement.setString(5, propertyListing.numberOfBathrooms)
      preparedStatement.setString(6, propertyListing.numberOfBedrooms)
      preparedStatement.setString(7, propertyListing.buildingType)
      preparedStatement.setInt(8, propertyListing.price)
      preparedStatement.setString(9, propertyListing.address)
      preparedStatement.setFloat(10, propertyListing.longitude)
      preparedStatement.setFloat(11, propertyListing.latitude)
      preparedStatement.setString(12, propertyListing.postalCode)
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

  def queryForChangedPrices(oldExecutionId: Long, newExecutionId: Long): List[PriceChangePropertyListing] = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(PropertyListingRepository.changedPriceStatement)
    preparedStatement.setLong(1, oldExecutionId)
    preparedStatement.setLong(2, newExecutionId)
    val resultSet = preparedStatement.executeQuery()
    val changedProperties = PropertyListingRepository.convertResultSetToPriceChangedProperty(resultSet)
    connection.close()
    changedProperties
  }

  def queryByExecutionId(executionId: Long): List[PropertyListing] = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(PropertyListingRepository.changedPriceStatement)
    preparedStatement.setLong(1, executionId)
    val resultSet = preparedStatement.executeQuery()
    val propertyListings = PropertyListingRepository.convertResultSet(resultSet)
    connection.close()
    propertyListings
  }

  def queryForNewProperties(oldExecutionId: Long, newExecutionId: Long): List[PropertyListing] = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(PropertyListingRepository.newPropertiesStatement)
    preparedStatement.setLong(1, newExecutionId)
    preparedStatement.setLong(2, oldExecutionId)
    val resultSet = preparedStatement.executeQuery()
    val propertyListings = PropertyListingRepository.convertResultSet(resultSet)
    connection.close()
    propertyListings
  }

}

package com.realestatetracker.repository

import java.sql.{Date, DriverManager, ResultSet}
import java.time.LocalDate

import com.realestatetracker.config.Config
import com.realestatetracker.entity.MongoSoldProperty

import scala.collection.mutable.ListBuffer


object MongoSoldPropertyRepository {
  private val insertStatement =
    """
      |insert into mongo_sold_property
      |(au_execution_id, mongo_id, mls_number, days_on_market, date_listed, listed_price, date_sold, sold_price)
      |values
      |(?, ?, ?, ?, ?, ?, ?, ?)
    """.stripMargin

  private val queryBySoldDateStatement =
    """
      |select * from mongo_sold_property
      |where date_sold = ?
    """.stripMargin


  private val queryByExecutionId =
    """
      |select * from mongo_sold_property
      |where au_execution_id = ?
    """.stripMargin

  private def convertResultSet(resultSet: ResultSet): List[MongoSoldProperty] = {
    val listBuffer = new ListBuffer[MongoSoldProperty]
    while (resultSet.next()) {
      listBuffer.append(MongoSoldProperty(
        resultSet.getLong("au_execution_id"),
        resultSet.getString("mongo_id"),
        resultSet.getString("mls_number"),
        resultSet.getInt("days_on_market"),
        resultSet.getDate("date_listed").toLocalDate,
        resultSet.getInt("listed_price"),
        resultSet.getDate("date_sold").toLocalDate,
        resultSet.getInt("sold_price")
      ))
    }
    listBuffer.toList
  }

}

class MongoSoldPropertyRepository {

  Class.forName(Config.databaseDriver)

  def insertSoldProperties(soldProperties: Seq[MongoSoldProperty]): Unit = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    connection.setAutoCommit(false)

    val preparedStatement = connection.prepareStatement(MongoSoldPropertyRepository.insertStatement)
    for (property <- soldProperties) {
      preparedStatement.setLong(1, property.executionId)
      preparedStatement.setString(2, property.mongoId)
      preparedStatement.setString(3, property.mlsNumber)
      preparedStatement.setInt(4, property.daysOnMarket)
      preparedStatement.setDate(5, Date.valueOf(property.dateListed))
      preparedStatement.setInt(6, property.listedPrice)
      preparedStatement.setDate(7, Date.valueOf(property.dateSold))
      preparedStatement.setInt(8, property.soldPrice)
      preparedStatement.addBatch()
    }

    preparedStatement.executeBatch()
    preparedStatement.clearBatch()
    connection.commit()
    connection.close()
  }

  def queryBySoldDate(date: LocalDate): List[MongoSoldProperty] = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)

    val preparedStatement = connection.prepareStatement(MongoSoldPropertyRepository.queryBySoldDateStatement)
    preparedStatement.setDate(1, Date.valueOf(date))
    val resultSet = preparedStatement.executeQuery()
    val soldProperties = MongoSoldPropertyRepository.convertResultSet(resultSet)

    connection.close()

    soldProperties
  }

  def queryByExecutionId(executionId: Long): List[MongoSoldProperty] = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(MongoSoldPropertyRepository.queryByExecutionId)
    preparedStatement.setLong(1, executionId)
    val resultSet = preparedStatement.executeQuery()
    val soldProperties = MongoSoldPropertyRepository.convertResultSet(resultSet)
    connection.close()
    soldProperties
  }


}

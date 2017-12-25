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
      |(mongo_id, mls_number, days_on_market, date_listed, listed_price, date_sold, sold_price)
      |values
      |(?, ?, ?, ?, ?, ?, ?)
    """.stripMargin

  private val queryBySoldDateStatement =
    """
      |select * from mongo_sold_property
      |where date_sold = ?
    """.stripMargin

  private def convertResultSet(resultSet: ResultSet): List[MongoSoldProperty] = {
    val listBuffer = new ListBuffer[MongoSoldProperty]
    while (resultSet.next()) {
      listBuffer.append(MongoSoldProperty(
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
      preparedStatement.setString(1, property.mongoId)
      preparedStatement.setString(2, property.mlsNumber)
      preparedStatement.setInt(3, property.daysOnMarket)
      preparedStatement.setDate(4, Date.valueOf(property.dateListed))
      preparedStatement.setInt(5, property.listedPrice)
      preparedStatement.setDate(6, Date.valueOf(property.dateSold))
      preparedStatement.setInt(7, property.soldPrice)
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


}

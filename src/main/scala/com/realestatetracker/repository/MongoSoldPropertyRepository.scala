package com.realestatetracker.repository

import java.sql.{Date, DriverManager}

import com.realestatetracker.config.Config
import com.realestatetracker.entity.MongoSoldProperty


object MongoSoldPropertyRepository {
  private val insertStatement =
    """
      |insert into mongo_sold_property
      |(mongo_id, mls_number, days_on_market, date_listed, listed_price, date_sold, sold_price)
      |values
      |(?, ?, ?, ?, ?, ?, ?)
    """.stripMargin
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


}

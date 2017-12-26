package com.realestatetracker.repository

import java.sql.DriverManager

import com.realestatetracker.config.Config
import com.realestatetracker.entity.SigmaSoldProperty

object SigmaSoldPropertyRepository {
  private val insertStatement =
    """
      |insert into sigma_sold_property
      |(au_execution_id, sigma_id, mls_number, latitude, longitude, listedPrice, soldPrice)
      |values
      |(?, ?, ?, ?, ?, ?, ?)
    """.stripMargin
}

class SigmaSoldPropertyRepository {

  Class.forName(Config.databaseDriver)

  def insertSoldProperties(soldProperties: Seq[SigmaSoldProperty]): Unit = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    connection.setAutoCommit(false)

    val preparedStatement = connection.prepareStatement(SigmaSoldPropertyRepository.insertStatement)
    for (property <- soldProperties) {
      preparedStatement.setLong(1, property.executionId)
      preparedStatement.setString(2, property.sigmaId)
      preparedStatement.setString(3, property.mlsNumber)
      preparedStatement.setFloat(4, property.latitude)
      preparedStatement.setFloat(5, property.longitude)
      preparedStatement.setString(6, property.listedPrice)
      preparedStatement.setInt(7, property.soldPrice)
      preparedStatement.addBatch()
    }

    preparedStatement.executeBatch()
    preparedStatement.clearBatch()
    connection.commit()
    connection.close()
  }

}

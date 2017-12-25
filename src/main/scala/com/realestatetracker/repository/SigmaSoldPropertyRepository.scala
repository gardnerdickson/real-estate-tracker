package com.realestatetracker.repository

import java.sql.DriverManager

import com.realestatetracker.config.Config
import com.realestatetracker.entity.SigmaSoldProperty

object SigmaSoldPropertyRepository {
  private val insertStatement =
    """
      |insert into sigma_sold_property
      |(sigma_id, mls_number, latitude, longitude, listedPrice, soldPrice)
      |values
      |(?, ?, ?, ?, ?, ?)
    """.stripMargin
}

class SigmaSoldPropertyRepository {

  Class.forName(Config.databaseDriver)

  def insertSoldProperties(soldProperties: Seq[SigmaSoldProperty]): Unit = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    connection.setAutoCommit(false)

    val preparedStatement = connection.prepareStatement(SigmaSoldPropertyRepository.insertStatement)
    for (property <- soldProperties) {
      preparedStatement.setString(1, property.sigmaId)
      preparedStatement.setString(2, property.mlsNumber)
      preparedStatement.setFloat(3, property.latitude)
      preparedStatement.setFloat(4, property.longitude)
      preparedStatement.setString(5, property.listedPrice)
      preparedStatement.setInt(6, property.soldPrice)
      preparedStatement.addBatch()
    }

    preparedStatement.executeBatch()
    preparedStatement.clearBatch()
    connection.commit()
    connection.close()
  }

}

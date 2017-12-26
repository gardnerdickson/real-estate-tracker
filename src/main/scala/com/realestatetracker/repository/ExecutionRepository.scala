package com.realestatetracker.repository

import java.sql.{Date, DriverManager, Timestamp}
import java.time.{LocalDate, LocalDateTime}

import com.realestatetracker.config.Config


object ExecutionRepository {

  private val createStatement =
    """
      |insert into execution
      |(au_start_timestamp, au_end_timestamp, au_status, date, minimum_price, maximum_price, minimum_latitude, maximum_latitude, minimum_longitude, maximum_longitude)
      |values
      |(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.stripMargin

  private val updateStatement =
    """
      |update execution
      | set au_status = ?,
      |     au_end_timestamp = ?
      |where au_execution_id = ?
    """.stripMargin
}


class ExecutionRepository {

  Class.forName(Config.databaseDriver)

  def createExecutionLog(date: LocalDate): Long = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)

    val preparedStatement = connection.prepareStatement(ExecutionRepository.createStatement)
    preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()))
    preparedStatement.setTimestamp(2, null)
    preparedStatement.setString(3, "STARTED")
    preparedStatement.setDate(4, Date.valueOf(date))
    preparedStatement.setInt(5, Config.minimumPrice)
    preparedStatement.setInt(6, Config.maximumPrice)
    preparedStatement.setFloat(7, Config.minimumLatitude)
    preparedStatement.setFloat(8, Config.maximumLatitude)
    preparedStatement.setFloat(9, Config.minimumLongitude)
    preparedStatement.setFloat(10, Config.maximumLongitude)

    preparedStatement.executeUpdate()
    val executionId = {
      val keys = preparedStatement.getGeneratedKeys
      keys.next()
      keys.getLong(1)
    }

    connection.commit()
    connection.close()
    executionId
  }

  def updateExecution(executionId: Long, status: String): Unit = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(ExecutionRepository.updateStatement)
    preparedStatement.setString(1, status)
    preparedStatement.setTimestamp(2, if ("COMPLETE" equals status) Timestamp.valueOf(LocalDateTime.now) else null)
    preparedStatement.setLong(3, executionId)

    preparedStatement.executeUpdate()
    connection.commit()
    connection.close()
  }

}

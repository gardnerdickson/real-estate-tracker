package com.realestatetracker.repository

import java.sql.{Date, DriverManager, ResultSet, Timestamp}
import java.time.{LocalDate, LocalDateTime}

import com.realestatetracker.config.Config
import com.realestatetracker.entity.Execution
import com.realestatetracker.request.{ExecutionStatus, ProcessType}

import scala.collection.mutable.ListBuffer


object ExecutionRepository {

  private val createStatement =
    """
      |insert into execution
      |(au_start_timestamp, au_end_timestamp, au_status, au_process_type, date, minimum_price, maximum_price, minimum_latitude, maximum_latitude, minimum_longitude, maximum_longitude)
      |values
      |(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.stripMargin

  private val latestCompleteExecutionStatement =
    """
      |select * from execution
      |where au_status = 'COMPLETE'
      |and au_process_type = ?
      |and date = ?
      |order by au_end_timestamp desc
      |limit 1
    """.stripMargin

  private val latestCompleteExecutionWithDateRangeStatement = {
    """
      |select * from execution
      |where au_status = 'COMPLETE'
      |and au_process_type = ?
      |and au_start_timestamp between ? and ?
      |order by au_end_timestamp desc
      |limit 1
    """.stripMargin
  }

  private val updateStatement =
    """
      |update execution
      | set au_status = ?,
      |     au_end_timestamp = ?
      |where au_execution_id = ?
    """.stripMargin


  private def convertResultSet(resultSet: ResultSet): List[Execution] = {
    val listBuffer = new ListBuffer[Execution]
    while (resultSet.next()) {
      listBuffer.append(
        Execution(
          resultSet.getLong("au_execution_id"),
          resultSet.getTimestamp("au_start_timestamp").toLocalDateTime,
          resultSet.getTimestamp("au_end_timestamp").toLocalDateTime,
          ExecutionStatus.valueOf(resultSet.getString("au_status")),
          ProcessType.valueOf(resultSet.getString("au_process_type")),
          resultSet.getDate("date").toLocalDate,
          resultSet.getInt("minimum_price"),
          resultSet.getInt("maximum_price"),
          resultSet.getInt("minimum_latitude"),
          resultSet.getInt("maximum_latitude"),
          resultSet.getInt("minimum_longitude"),
          resultSet.getInt("maximum_longitude")
        )
      )
    }
    listBuffer.toList
  }
}


class ExecutionRepository {

  Class.forName(Config.databaseDriver)

  def createExecutionLog(date: LocalDate, processType: ProcessType): Long = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)

    val preparedStatement = connection.prepareStatement(ExecutionRepository.createStatement)
    preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()))
    preparedStatement.setTimestamp(2, null)
    preparedStatement.setString(3, ExecutionStatus.STARTED.name())
    preparedStatement.setString(4, processType.name())
    preparedStatement.setDate(5, Date.valueOf(date))
    preparedStatement.setInt(6, Config.minimumPrice)
    preparedStatement.setInt(7, Config.maximumPrice)
    preparedStatement.setFloat(8, Config.minimumLatitude)
    preparedStatement.setFloat(9, Config.maximumLatitude)
    preparedStatement.setFloat(10, Config.minimumLongitude)
    preparedStatement.setFloat(11, Config.maximumLongitude)

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

  def updateExecution(executionId: Long, status: ExecutionStatus): Unit = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(ExecutionRepository.updateStatement)
    preparedStatement.setString(1, status.name())
    preparedStatement.setTimestamp(2, if (ExecutionStatus.COMPLETE equals status) Timestamp.valueOf(LocalDateTime.now) else null)
    preparedStatement.setLong(3, executionId)

    preparedStatement.executeUpdate()
    connection.commit()
    connection.close()
  }

  def getLatestCompleteExecution(processType: ProcessType, date: LocalDate): Option[Execution] = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(ExecutionRepository.latestCompleteExecutionStatement)
    preparedStatement.setString(1, processType.name())
    preparedStatement.setDate(2, Date.valueOf(date))
    val resultSet = preparedStatement.executeQuery()
    val executions = ExecutionRepository.convertResultSet(resultSet)
    connection.close()
    executions.headOption
  }

  def getLatestCompleteExecution(processType: ProcessType, lowerStartDateTime: LocalDateTime, upperStartDateTime: LocalDateTime): Option[Execution] = {
    val connection = DriverManager.getConnection(Config.databaseConnection, Config.databaseUsername, Config.databasePassword)
    val preparedStatement = connection.prepareStatement(ExecutionRepository.latestCompleteExecutionWithDateRangeStatement)
    preparedStatement.setString(1, processType.name())
    preparedStatement.setTimestamp(2, Timestamp.valueOf(lowerStartDateTime))
    preparedStatement.setTimestamp(3, Timestamp.valueOf(upperStartDateTime))
    val resultSet = preparedStatement.executeQuery()
    val executions = ExecutionRepository.convertResultSet(resultSet)
    connection.close()
    executions.headOption
  }

}

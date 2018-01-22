package com.realestatetracker.main

import java.time.LocalDate

import com.realestatetracker.config.Config
import com.realestatetracker.repository.ExecutionRepository
import com.realestatetracker.request.{ExecutionStatus, ProcessType}
import com.typesafe.scalalogging.LazyLogging

class Process(val processType: ProcessType, val runner: (Long, LocalDate) => Unit) extends LazyLogging {

  def run(date: LocalDate): Unit = {

    val executionRepository = new ExecutionRepository()

    val executionId = try {
      executionRepository.createExecutionLog(date, processType)
    } catch {
      case e: Exception =>
        logger.error("Failed to initialize application.", e)
        throw e
    }

    try {
      runner.apply(executionId, date)
      executionRepository.updateExecution(executionId, ExecutionStatus.COMPLETE)
    } catch {
      case e: Exception =>
        logger.error("Unhandled exception caught. Setting execution to 'FAILED'")
        executionRepository.updateExecution(executionId, ExecutionStatus.FAILED)
        throw e
    }

  }
}

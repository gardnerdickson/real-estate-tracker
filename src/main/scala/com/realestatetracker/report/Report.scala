package com.realestatetracker.report

import java.time.LocalDate

import com.realestatetracker.entity.PriceChangePropertyListing
import com.realestatetracker.main.ReportGenerator.{executionRepository, propertyListingRepository}
import com.realestatetracker.repository.{ExecutionRepository, MongoSoldPropertyRepository, PropertyListingRepository}
import com.realestatetracker.request.ProcessType

trait Report {

  def generate(): String

  protected def createTitle(title: String): String = {
    new StringBuilder()
      .append("--").append("-" * title.length).append("--").append(Report.newLine)
      .append("| ").append(title).append(" |").append(Report.newLine)
      .append("--").append("-" * title.length).append("--").append(Report.newLine)
      .toString()
  }
}

object Report {
  val newLine: String = System.getProperty("line.separator")
}

class MongoSoldPropertiesReport(date: LocalDate) extends Report {

  private val mongoSoldPropertyRepository = new MongoSoldPropertyRepository
  private val executionRepository = new ExecutionRepository

  override def generate(): String = {

    val execution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, date)
    if (execution.isEmpty) {
      throw new RuntimeException(s"No executions for date: $date, process type: ${ProcessType.DOWNLOAD_PROPERTIES}")
    }

    val soldProperties = mongoSoldPropertyRepository.queryByExecutionId(execution.get.executionId)

    val report = new StringBuilder()
      .append(createTitle(s"MLS LISTINGS SOLD ON $date"))
      .append(Report.newLine)
    soldProperties.map(_.mlsNumber).foreach(num => report.append(num).append(Report.newLine))

    report.toString()
  }
}


class RealtorPropertiesReport(executionId: Long, date: LocalDate) extends Report {

  private val repository = new PropertyListingRepository

  override def generate(): String = {
    val propertyListings = repository.queryByDateCreated(date)
    val report = new StringBuilder()
      .append(createTitle(s"MLS PROPERTIES AS OF $date"))
      .append(Report.newLine)
    propertyListings.map(_.mlsNumber).foreach(num => report.append(num).append(Report.newLine))
    report.toString()
  }

}


class RealtorChangedPricesReport() extends Report {

  private val propertyListingRepository = new PropertyListingRepository
  private val executionRepository = new ExecutionRepository

  override def generate(): String = {

    val previousLower = LocalDate.now().minusDays(1).atStartOfDay()
    val previousUpper = LocalDate.now().atStartOfDay().minusMinutes(1)

    val lower = LocalDate.now().atStartOfDay()
    val upper = LocalDate.now().plusDays(1).atStartOfDay().minusMinutes(1)
    val oldExecution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, previousLower, previousUpper)
    if (oldExecution.isEmpty) {
      throw new RuntimeException(s"No executions for date range: $previousLower to $previousUpper, process type: ${ProcessType.DOWNLOAD_PROPERTIES}")
    }

    val newExecution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, lower, upper)
    if (newExecution.isEmpty) {
      throw new RuntimeException(s"No executions for date range: $lower to $upper, process type: ${ProcessType.DOWNLOAD_PROPERTIES}")
    }

    val changedProperties = propertyListingRepository.queryForChangedPrices(oldExecution.get.executionId, newExecution.get.executionId)

    val report = new StringBuilder()
      .append(createTitle(s"MLS PROPERTIES WITH PRICE CHANGES - ${previousLower.toLocalDate} COMPARED TO ${lower.toLocalDate}"))
      .append(Report.newLine)
    changedProperties.map(prop => s"${prop.mlsNumber} change from ${prop.oldPrice} to ${prop.newPrice}").foreach(line => report.append(line).append(Report.newLine))
    report.toString()
  }

}

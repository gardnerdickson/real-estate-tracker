package com.realestatetracker.main

import java.nio.file.Paths
import java.time.{LocalDate, LocalDateTime}

import com.realestatetracker.config.Config
import com.realestatetracker.entity.{PriceChangePropertyListing, PropertyListing}
import com.realestatetracker.report.{GmailReportMailer, MongoSoldPropertiesReport, RealtorChangedPricesReport, ReportWriter}
import com.realestatetracker.repository.{ExecutionRepository, PropertyListingRepository}
import com.realestatetracker.request.ProcessType
import com.typesafe.scalalogging.LazyLogging

object ReportGenerator extends LazyLogging {

  private val executionRepository = new ExecutionRepository
  private val propertyListingRepository = new PropertyListingRepository

  def main(args: Array[String]): Unit = {

    val process = new Process(ProcessType.GENERATE_REPORT, (executionId, date) => {
      val execution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, date)
      if (execution.isEmpty) {
        throw new RuntimeException(s"No executions for date: $date, process type: ${ProcessType.DOWNLOAD_PROPERTIES.name()}")
      }
      logger.info(s"Latest execution ID for ${ProcessType.DOWNLOAD_PROPERTIES.name()} is ${execution.get.executionId}")
      generateAndSendReport(execution.get.executionId, date)
    })
    process.run(args)
  }

  private def generateAndSendReport(executionId: Long, date: LocalDate): Unit = {
    val reportWriter = new ReportWriter(
      new RealtorChangedPricesReport(),
      new MongoSoldPropertiesReport(date)
    )

    val reportFile = Paths.get(Config.reportDirectory, Config.reportFile(date))
    reportWriter.write(reportFile)

//    val reportMailer = new GmailReportMailer(
//      Config.emailUsername,
//      Config.emailPassword,
//      Config.googleApplicationName,
//      Paths.get(Config.configDirectory, Config.googleSecretFile),
//      Paths.get(Config.configDirectory, Config.googleCredentialDataStore)
//    )
//    reportMailer.sendEmail("Test", Config.emailFromAddress, Config.emailRecipients, "This is another test")
  }
}

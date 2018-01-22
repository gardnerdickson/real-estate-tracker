package com.realestatetracker.main

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.time.LocalDate

import com.realestatetracker.config.Config
import com.realestatetracker.report._
import com.realestatetracker.repository.ExecutionRepository
import com.realestatetracker.request.ProcessType
import com.typesafe.scalalogging.LazyLogging

object ReportGenerator extends LazyLogging {

  private val executionRepository = new ExecutionRepository

  def getProcess: Process = {
    new Process(ProcessType.GENERATE_REPORT, (executionId, date) => {
      val execution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, date)
      if (execution.isEmpty) {
        throw new RuntimeException(s"No executions for date: $date, process type: ${ProcessType.DOWNLOAD_PROPERTIES.name()}")
      }
      logger.info(s"Latest execution ID for ${ProcessType.DOWNLOAD_PROPERTIES.name()} is ${execution.get.executionId}")
      generateAndSendReport(execution.get.executionId, date)
    })
  }

  private def generateAndSendReport(executionId: Long, date: LocalDate): Unit = {
    val reports = List(
      new RealtorChangedPricesReport(date),
      new RealtorNewPropertiesReport(date)
//      new MongoSoldPropertiesReport(date)
    )

    val sectionedReports = reports.map(_.createReportSections)
    logger.info("Creating the text report.")
    val textReport = Report.createTextReport(sectionedReports)
    logger.info("Creating the HTML report.")
    val htmlReport = Report.createHtmlReport(sectionedReports)

    logger.info("Printing text report to stdout")
    println(textReport)

    val reportFile = Paths.get(Config.reportDirectory, Config.reportFile(date))
    logger.info(s"Writing text report to file: $reportFile")
    val openOption = if (Files.exists(reportFile)) StandardOpenOption.TRUNCATE_EXISTING else StandardOpenOption.CREATE
    Files.write(reportFile, textReport.getBytes(StandardCharsets.UTF_8), openOption)

    logger.info(s"Sending HTML report as email to ${Config.emailRecipients.mkString(", ")}")
    val reportMailer = new GmailReportMailer(
      Config.emailUsername,
      Config.emailPassword,
      Config.googleApplicationName,
      Paths.get(Config.configDirectory, Config.googleSecretFile),
      Paths.get(Config.configDirectory, Config.googleCredentialDataStore)
    )
    val emailTitle = s"[real-estate-tracker] Report for $date"
    reportMailer.sendEmail(emailTitle, Config.emailFromAddress, Config.emailRecipients, htmlReport)
  }
}

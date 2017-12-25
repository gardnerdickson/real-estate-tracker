package com.realestatetracker.main

import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.realestatetracker.config.Config
import com.realestatetracker.report.{MongoSoldPropertiesReport, RealtorChangedPricesReport, RealtorPropertiesReport, ReportWriter}

object ReportGenerator {

  def main(args: Array[String]): Unit = {
    val date = LocalDate.parse(args(0), DateTimeFormatter.ISO_LOCAL_DATE)

    val reportWriter = new ReportWriter(
      new MongoSoldPropertiesReport(date),
      new RealtorPropertiesReport(date),
      new RealtorChangedPricesReport(date)
    )

    val reportFile = Paths.get(Config.reportDirectory, Config.reportFile(date))
    reportWriter.write(reportFile)
  }
}

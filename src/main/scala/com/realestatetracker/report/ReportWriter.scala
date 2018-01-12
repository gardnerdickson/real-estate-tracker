package com.realestatetracker.report

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardOpenOption}

class ReportWriter(reports: Report*) {

  def write(path: Path): Unit = {
    val reportBuilder = new StringBuilder()
    reports.foreach(report => reportBuilder.append(report.generate()).append(Report.newLine))

    val openOption = if (Files.exists(path)) StandardOpenOption.TRUNCATE_EXISTING else StandardOpenOption.CREATE
    Files.write(path, reportBuilder.toString.getBytes(StandardCharsets.UTF_8), openOption)
  }
}

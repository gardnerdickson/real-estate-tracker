package com.realestatetracker.main

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.realestatetracker.config.Config
import com.realestatetracker.request.ProcessType

import scala.util.{Failure, Success, Try}

object Main {

  def main(args: Array[String]): Unit = {

    val mode = Try(ProcessType.valueOf(args(0))) match {
      case Success(processType) => processType
      case Failure(e) => throw new IllegalArgumentException(s"Cannot resolve process type: ${args(0)}")
    }
    val date = LocalDate.parse(args(1), Config.commandLineDateFormat).minusDays(1)

    val process = if (mode equals ProcessType.DOWNLOAD_PROPERTIES) {
      PropertyDownloader.getProcess
    } else {
      ReportGenerator.getProcess
    }

    process.run(date)

  }
}

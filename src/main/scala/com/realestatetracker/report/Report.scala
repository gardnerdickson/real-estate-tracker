package com.realestatetracker.report

import java.time.LocalDate

import com.realestatetracker.repository.{MongoSoldPropertyRepository, PropertyListingRepository}

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

  private val repository = new MongoSoldPropertyRepository

  override def generate(): String = {
    val soldProperties = repository.queryBySoldDate(date)

    val report = new StringBuilder()
      .append(createTitle(s"MLS LISTINGS SOLD ON $date"))
      .append(Report.newLine)
    soldProperties.map(_.mlsNumber).foreach(num => report.append(num).append(Report.newLine))

    report.toString()
  }
}


class RealtorPropertiesReport(date: LocalDate) extends Report {

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


class RealtorChangedPricesReport(date: LocalDate) extends Report {

  private val repository = new PropertyListingRepository

  override def generate(): String = {
    val oldDate = date.minusDays(1)
    val changedProperties = repository.queryForChangedPrices(oldDate, date)
    val report = new StringBuilder()
      .append(createTitle(s"MLS PROPERTIES WITH PRICE CHANGES - $oldDate COMPARED TO $date"))
      .append(Report.newLine)
    changedProperties.map(prop => s"${prop.mlsNumber} change from ${prop.oldPrice} to ${prop.newPrice}").foreach(line => report.append(line).append(Report.newLine))
    report.toString()
  }

}

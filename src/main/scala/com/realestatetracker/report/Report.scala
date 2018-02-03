package com.realestatetracker.report

import java.time.LocalDate

import com.realestatetracker.config.Config
import com.realestatetracker.entity.PriceChangePropertyListing
import com.realestatetracker.repository.{ExecutionRepository, MongoSoldPropertyRepository, PropertyListingRepository}
import com.realestatetracker.request.ProcessType
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable.ListBuffer

trait Report {
  def createReportSections: List[ReportSection]
}

trait ReportSection {
  def isTitle: Boolean

  def isSubtitle: Boolean

  def isSection: Boolean

  def content: String
}

case class ReportTitle(title: String) extends ReportSection {
  override def isTitle: Boolean = true

  override def isSubtitle: Boolean = false

  override def isSection: Boolean = false

  override def content: String = title
}

case class ReportSubtitle(subtitle: String) extends ReportSection {
  override def isTitle: Boolean = false

  override def isSubtitle: Boolean = true

  override def isSection: Boolean = false

  override def content: String = subtitle
}

case class ReportBodySection(section: String) extends ReportSection {
  override def isTitle: Boolean = false

  override def isSubtitle: Boolean = false

  override def isSection: Boolean = true

  override def content: String = section
}


object Report {
  val newLine: String = System.getProperty("line.separator")

  def createTextReport(sectionedReports: List[List[ReportSection]]): String = {

    def createTitle(title: String): String = {
      new StringBuilder()
        .append("--").append("-" * title.length).append("--").append(Report.newLine)
        .append("| ").append(title).append(" |").append(Report.newLine)
        .append("--").append("-" * title.length).append("--").append(Report.newLine)
        .toString()
    }

    def createSubtitle(title: String): String = {
      new StringBuilder()
        .append(title).append(Report.newLine).append("-" * title.length)
        .toString()
    }

    val reportBuilder = new StringBuilder

    for (sections <- sectionedReports) {
      for (section <- sections) {
        section match {
          case ReportTitle(title) => reportBuilder.append(createTitle(title)).append(Report.newLine * 2)
          case ReportSubtitle(subtitle) => reportBuilder.append(createSubtitle(subtitle)).append(Report.newLine * 2)
          case ReportBodySection(bodySection) => reportBuilder.append(bodySection).append(Report.newLine * 2)
        }
      }
      reportBuilder.append(Report.newLine)
    }

    reportBuilder.toString()
  }

  def createHtmlReport(sectionedReports: List[List[ReportSection]]): String = {
    val reportBuilder = new StringBuilder()
    for (sections <- sectionedReports) {
      reportBuilder.append("""<table style="font-family: 'Lucida Console', Monaco, monospace">""")
      for (section <- sections) {
        reportBuilder.append("<tr><td>")
        section match {
          case ReportTitle(title) => reportBuilder.append(s"<h2>$title</h2>")
          case ReportSubtitle(subtitle) => reportBuilder.append(s"<h3>$subtitle</h3>")
          case ReportBodySection(bodySection) => reportBuilder.append(bodySection.replace("\n", "<br/>"))
        }
        reportBuilder.append("</td></tr>")
      }
      reportBuilder.append("</table>")
    }
    reportBuilder.toString()
  }
}

class MongoSoldPropertiesReport(val date: LocalDate) extends Report with LazyLogging {

  private val mongoSoldPropertyRepository = new MongoSoldPropertyRepository
  private val executionRepository = new ExecutionRepository

  override def createReportSections: List[ReportSection] = {
    logger.info(s"Getting latest complete execution for ${ProcessType.DOWNLOAD_PROPERTIES}")
    val execution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, date)
    if (execution.isEmpty) {
      throw new RuntimeException(s"No executions for date: $date, process type: ${ProcessType.DOWNLOAD_PROPERTIES}")
    }
    logger.info(s"Latest execution is ${execution.get.executionId}")

    logger.info("Getting Mongo properties")
    val soldProperties = mongoSoldPropertyRepository.queryByExecutionId(execution.get.executionId)

    val sections = new ListBuffer[ReportSection]
    sections.append(ReportTitle(s"MLS LISTINGS SOLD ON $date"))
    soldProperties.foreach(property => sections.append(ReportBodySection(property.mlsNumber.toString)))
    sections.toList
  }
}


class RealtorChangedPricesReport(val date: LocalDate) extends Report with LazyLogging {

  private val propertyListingRepository = new PropertyListingRepository
  private val executionRepository = new ExecutionRepository

  override def createReportSections: List[ReportSection] = {

    def createSection(changedPriceProperty: PriceChangePropertyListing): String = {
      s"""
         |MLS Number: ${changedPriceProperty.property.mlsNumber}
         |New Price: ${changedPriceProperty.newPrice}
         |Old Price: ${changedPriceProperty.oldPrice}
         |Price Difference: ${Math.abs(changedPriceProperty.newPrice - changedPriceProperty.oldPrice)}
         |Description: ${changedPriceProperty.property.description}
         |Postal Code: ${changedPriceProperty.property.postalCode}
         |Link: ${Config.condosDotCaLink(changedPriceProperty.property.mlsNumber)}
      """.stripMargin
    }

    logger.info("Getting latest and previous execution IDs")
    val previousDate = date.minusDays(1)
    val oldExecution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, previousDate)
    if (oldExecution.isEmpty) {
      throw new RuntimeException(s"No executions for date: $previousDate, process type: ${ProcessType.DOWNLOAD_PROPERTIES}")
    }
    val newExecution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, date)
    if (newExecution.isEmpty) {
      throw new RuntimeException(s"No executions for date: $date, process type: ${ProcessType.DOWNLOAD_PROPERTIES}")
    }
    logger.info(s"Latest execution ID is ${oldExecution.get.executionId}. Previous is ${newExecution.get.executionId}")

    logger.info("Getting properties with changed prices.")
    val changedProperties = propertyListingRepository.queryForChangedPrices(oldExecution.get.executionId, newExecution.get.executionId)
    val priceDecreaseProperties = new ListBuffer[PriceChangePropertyListing]
    val priceIncreaseProperties = new ListBuffer[PriceChangePropertyListing]
    for (property <- changedProperties) {
      if (property.newPrice <= property.oldPrice) {
        priceDecreaseProperties.append(property)
      } else {
        priceIncreaseProperties.append(property)
      }
    }
    logger.info(s"Number of properties with decreased prices: ${priceDecreaseProperties.size}")
    logger.info(s"Number of properties with increased prices: ${priceIncreaseProperties.size}")

    val sections = new ListBuffer[ReportSection]
    sections.append(ReportTitle(s"MLS PROPERTIES WITH PRICE CHANGES - $previousDate COMPARED TO $date"))
    sections.append(ReportSubtitle("The price for the following properties decreased"))
    priceDecreaseProperties.map(createSection).foreach(section => sections.append(ReportBodySection(section)))
    sections.append(ReportSubtitle("The price for the following properties increased"))
    priceIncreaseProperties.map(createSection).foreach(section => sections.append(ReportBodySection(section)))

    sections.toList
  }
}


class RealtorNewPropertiesReport(val date: LocalDate) extends Report with LazyLogging {

  private val propertyListingRepository = new PropertyListingRepository
  private val executionRepository = new ExecutionRepository

  override def createReportSections: List[ReportSection] = {
    logger.info("Getting latest and previous execution IDs")
    val previousDate = date.minusDays(1)
    val oldExecution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, previousDate)
    if (oldExecution.isEmpty) {
      throw new RuntimeException(s"No executions for date: $previousDate, process type: ${ProcessType.DOWNLOAD_PROPERTIES}")
    }
    val newExecution = executionRepository.getLatestCompleteExecution(ProcessType.DOWNLOAD_PROPERTIES, date)
    if (newExecution.isEmpty) {
      throw new RuntimeException(s"No executions for date: $date, process type: ${ProcessType.DOWNLOAD_PROPERTIES}")
    }
    logger.info(s"Latest execution ID is ${oldExecution.get.executionId}. Previous is ${newExecution.get.executionId}")

    logger.info("Getting new properties.")
    val newProperties = propertyListingRepository.queryForNewProperties(oldExecution.get.executionId, newExecution.get.executionId)
    logger.info(s"Found ${newProperties.size} new properties.")

    val sections = new ListBuffer[ReportSection]
    sections.append(ReportTitle(s"NEW MLS PROPERTIES ON $date"))
    newProperties
      .map(prop => {
        s"""
           |MLS Number: ${prop.mlsNumber}
           |Price: ${prop.price}
           |Description: ${prop.description}
           |Postal Code: ${prop.postalCode}
           |Link: ${Config.condosDotCaLink(prop.mlsNumber)}
      """.stripMargin
      })
      .foreach(section => sections.append(ReportBodySection(section)))

    sections.toList
  }

}


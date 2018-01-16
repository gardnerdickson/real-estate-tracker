package com.realestatetracker.report

import java.io.{ByteArrayOutputStream, InputStreamReader}
import java.nio.file.{Files, Path}
import java.util.Properties
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Message, Session}

import com.google.api.client.util.Base64
import com.typesafe.scalalogging.LazyLogging
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.gmail.{Gmail, GmailScopes}

import scala.collection.JavaConverters._

trait ReportMailer {

}

object GmailReportMailer {
  private val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
  private val JSON_FACTORY = JacksonFactory.getDefaultInstance
  private val SCOPES = List(GmailScopes.GMAIL_SEND)
}

class GmailReportMailer(val username: String, val password: String, val applicationName: String, val secretFile: Path, val dataStore: Path) extends LazyLogging {

  def sendEmail(subject: String, fromAddress: String, toAddresses: Array[String], messageText: String): Unit = {

    logger.info("Creating email")
    val gmailMessage = createEmail(subject, fromAddress, toAddresses, messageText)

    logger.info("Authorizing app with Google")
    val credential = authorize()
    val gmailService = new Gmail.Builder(GmailReportMailer.HTTP_TRANSPORT, GmailReportMailer.JSON_FACTORY, credential)
      .setApplicationName(applicationName)
      .build()

    logger.info("Sending the email")
    val response = gmailService.users().messages().send(fromAddress, gmailMessage).execute()
    logger.info(s"Gmail response ID: ${response.getId}")
  }

  private def authorize(): Credential = {
    val dataStoreFactory = new FileDataStoreFactory(dataStore.toFile)

    val inputStream = Files.newInputStream(secretFile)
    val clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance, new InputStreamReader(inputStream))
    val googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
      GmailReportMailer.HTTP_TRANSPORT,
      GmailReportMailer.JSON_FACTORY,
      clientSecrets,
      GmailReportMailer.SCOPES.asJava
    )
      .setDataStoreFactory(dataStoreFactory)
      .setAccessType("offline")
      .build()

    new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, new LocalServerReceiver()).authorize("user")
  }

  private def createEmail(subject: String, fromAddress: String, toAddresses: Array[String], messageText: String): com.google.api.services.gmail.model.Message = {
    val session = Session.getInstance(new Properties(), null)
    val email = new MimeMessage(session)
    email.setFrom(new InternetAddress(fromAddress))
    for (address <- toAddresses) {
      email.addRecipient(Message.RecipientType.TO, new InternetAddress(address))
    }
    email.setSubject(subject)
    email.setText(messageText, "UTF-8", "html")

    val buffer = new ByteArrayOutputStream()
    email.writeTo(buffer)
    val bytes = buffer.toByteArray
    val encodedEmail = Base64.encodeBase64URLSafeString(bytes)

    new com.google.api.services.gmail.model.Message().setRaw(encodedEmail)
  }


}

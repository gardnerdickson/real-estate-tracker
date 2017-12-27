package com.realestatetracker.report

import java.util.Properties
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeMessage}


trait ReportMailer {

}

class GmailReportMailer(username: String, password: String) {

  def sendEmail(subject: String, fromAddress: String, toAddresses: Array[String], messageText: String): Unit = {

    val properties = new Properties()
    properties.put("mail.smtp.host", "smtp.gmail.com")
    properties.put("mail.smtp.auth", "true")
    properties.put("mail.smtp.port", "587")
    properties.put("mail.smtp.auth", "true")
    properties.put("mail.smtp.starttls.enable", "true")

    val session = Session.getInstance(properties, new Authenticator {
      override protected def getPasswordAuthentication: PasswordAuthentication = {
        new PasswordAuthentication(username, password) // do some sort of encryption/decryption for the password
      }
    })

    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(fromAddress))
    for (address <- toAddresses) {
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(address))
    }
    message.setSubject(subject)
    message.setText(messageText)
    Transport.send(message)
  }

}

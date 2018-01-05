name := "real-estate-tracker"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.4"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.9.2"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.2"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.2"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.h2database" % "h2" % "1.4.196"
libraryDependencies += "javax.mail" % "mail" % "1.4.7"
libraryDependencies += "com.google.api-client" % "google-api-client" % "1.23.0"
libraryDependencies += "com.google.oauth-client" % "google-oauth-client-jetty" % "1.23.0"
libraryDependencies += "com.google.apis" % "google-api-services-gmail" % "v1-rev76-1.23.0"

name := "VeganMentor"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.5"

libraryDependencies += "junit" % "junit" % "4.12" % Test

libraryDependencies += "com.google.apis" % "google-api-services-gmail" % "v1-rev75-1.23.0"
libraryDependencies += "com.google.api-client" % "google-api-client" % "1.23.0"
libraryDependencies += "com.google.oauth-client" % "google-oauth-client-jetty" % "1.23.0"
libraryDependencies += "javax.mail" % "javax.mail-api" % "1.6.0"
libraryDependencies += "com.sun.mail" % "javax.mail" % "1.6.0"

libraryDependencies += "net.liftweb" %% "lift-json" % "3.2.0-M3"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.2.0"

libraryDependencies += "org.mongodb" %% "casbah" % "3.1.1"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.6.4"


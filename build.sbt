ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "WebScraper"
  )

libraryDependencies += "org.jsoup" % "jsoup" % "1.14.3"

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "3.0.0"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.0"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test

libraryDependencies += "org.apache.commons" % "commons-csv" % "1.10.0"

import sbt._
import Keys._



object Dependencies {
  val seleniumVersion = "2.21.0"
  val selenium = Seq(
    "org.seleniumhq.selenium" % "selenium-firefox-driver" % seleniumVersion,
    "org.seleniumhq.selenium" % "selenium-server"         % seleniumVersion
  )

  val jodaTime = Seq(
    "org.joda" % "joda-convert" % "1.2",
    "joda-time" % "joda-time" % "2.0"
  )

  val scalaTest = "org.scalatest" %% "scalatest" % "1.7.1" % "test"
}

import sbt._
import Keys._

import Helpers._
import Dependencies._


object Core extends Build {
  lazy val root = project(
    "Core",
    Seq(
      selenium,
      jodaTime,
      scalaTest
    )
  ) settings(
    initialCommands := """
import net.selenate._
import core._
import util._
import org.openqa.selenium._
val d = new BaseDriver()
"""
  )
}

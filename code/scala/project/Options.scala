import sbt._
import Keys._



object Options {
  val Name            = "Selenate"
  val Organisation    = "net.selenate"
  val Version         = "0.0.0"
  val ScalaVersions   = Seq("2.9.1", "2.9.0-1", "2.9.0")
  val Repositories    = Seq(
    "Maven Central" at "http://repo1.maven.org/maven2/"
  )
}
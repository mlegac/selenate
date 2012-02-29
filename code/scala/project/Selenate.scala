import sbt._
import Keys._

import Helpers._



object Selenate extends Build {
  lazy val root = top(
    Seq(
      Core.root
    )
  )
}

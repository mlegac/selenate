package net.selenate
package core

import org.joda.time.DateTime
import org.openqa.selenium.{ Cookie, OutputType }
import org.openqa.selenium.firefox.FirefoxDriver

import scala.collection.JavaConversions._



object Capturable {
  val dtFormat = "dd.MM.yyyy"
}


trait Capturable {
  self: FirefoxDriver =>
  import Capturable._

  protected case class CaptureData(
      dt: DateTime,
      html: String,
      screen: Array[Byte],
      cookieSet: Set[Cookie]) {
    override def toString =
      "CaptureData(Time = %s; html len = %s)".
      format(dt.toString(dtFormat), html.length)

    lazy val cookieStr = cookieSet mkString "\n"
  }


  def capture(name: String): CaptureData = {
    val html       = getPageSource
    val screen     = getScreenshotAs(OutputType.BYTES)
    val cookieList = manage.getCookies.toSet

    CaptureData(DateTime.now, html, screen, cookieList)
  }
}
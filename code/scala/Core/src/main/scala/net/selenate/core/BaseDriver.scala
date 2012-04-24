package net.selenate
package core

import org.openqa.selenium.firefox.{ FirefoxDriver, FirefoxProfile }
import scala.collection.JavaConversions._
import org.openqa.selenium.OutputType
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/** Provides utility methods for `BaseDriver`. */
object BaseDriver {
  private def InitFP(fpOpt: Option[FirefoxProfile]): FirefoxProfile =
    fpOpt.getOrElse(new FirefoxProfile())
}



/** Base ''Selenate'' class, based on `FirefoxDriver`.
  * Provides basic, low-level functionality.
  */
class BaseDriver protected(fpOpt: Option[FirefoxProfile], settings: SelenateSettings)
    extends FirefoxDriver(BaseDriver.InitFP(fpOpt)) {

  def locatorExistsBase(locator: Locator): Boolean = {
    val foundElementList = locator.componentSet filter elementExists
    val missingElementList = locator.componentSet &~ foundElementList

    val missingRatio = missingElementList.size.toDouble / locator.componentSet.size.toDouble
    missingRatio < settings.locatorMissingTreshold
  }

  def locatorListExistsBase(locatorList: Seq[Locator]): Boolean =
    locatorList map locatorExistsBase _ contains true



  protected def elementExists(by: By): Boolean =
    tryFindElement(by).isDefined

  protected def tryFindElement(by: By): Option[WebElement] =
    try {
      Some(findElement(by))
    } catch {
      case e: Exception => None
    }


  protected def waitFor =
    util.waitFor(settings.timeout, settings.resolution)
  protected def waitOrFail =
    util.waitOrFail(settings.timeout, settings.resolution)
}

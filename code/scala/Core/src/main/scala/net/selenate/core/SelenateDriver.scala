package net.selenate
package core

import org.openqa.selenium.firefox.{ FirefoxDriver, FirefoxProfile }
import scala.collection.JavaConversions._
import org.openqa.selenium.OutputType
import org.openqa.selenium.By
import org.openqa.selenium.WebElement


/** Storage class for `SelenateDriver` settings.
  *
  * @param timeout used when waiting for web page elements to load (maximum waiting time)
  * @param resolution used when waiting for web page elements to load (pause between successive checks)
  */
case class SelenateSettings(
    timeout: Long,
    resolution: Long,
    locatorMissingTreshold: Double)


/** Provides defaults, and utility methods for `SelenateDriver`. */
object SelenateDriver {
  /** Default settings for `SelenateDriver` (timeout of 30 s and resolution of 250 ms. */
  val DefaultSettings = SelenateSettings(30000L, 250L, 0.5)
}



/** Main ''Selenate'' class, based on `FirefoxDriver`. */
class SelenateDriver private(fpOpt: Option[FirefoxProfile], settings: SelenateSettings)
    extends BaseDriver(fpOpt, settings)
    with Capturable {

  def this(settings: SelenateSettings = SelenateDriver.DefaultSettings) =
    this(None, settings)
  def this(
      fp: FirefoxProfile,
      settings: SelenateSettings = SelenateDriver.DefaultSettings) =
    this(Some(fp), settings)


  /** Waits for the specified locator to appear.
    *
    * @param locator locator to wait for
    * @throws SelenateException if the locator is never found
    */
  def waitForLocator(locator: Locator) {
    waitOrFail(locator.name) {
      locatorExistsBase(locator)
    }
  }

  /** Waits for any locator from the specified list.
    *
    * @param locatorList a sequence of locators to wait for
    * @throws SelenateException if none of the locators are ever found
    */
  def waitForLocatorList(locatorList: Seq[Locator]) {
    val politeName = locatorList.map(_.name) mkString " or "
    waitOrFail(politeName) {
      locatorListExistsBase(locatorList)
    }
  }


  /** Checks weather the specified locator currently exists.
    *
    * @param locator locator to search for
    * @return true if the locator exists; false otherwise
    */
  def locatorExists(locator: Locator) = {
    locatorExistsBase(locator)
  }

  /** Checks weather any locator from the specified list currently exists.
    *
    * @param locatorList a sequence of locators to search for
    * @return true if any of the locators exist; false otherwise
    */
  def locatorListExists(locatorList: Seq[Locator]) = {
    locatorListExistsBase(locatorList)
  }



  /** Catches and logs any exceptions that occur in the specified function.
    * All exceptions that occur in the `failsafe` block are caught and captured.
    *
    * @param f code block to wrap
    */
  def failsafe[T](f: => T): T =
    try {
      f
    } catch {
      case e: Exception =>
        capture("EXCEPTION: " + e.getClass.getName)
        throw e
    }
}

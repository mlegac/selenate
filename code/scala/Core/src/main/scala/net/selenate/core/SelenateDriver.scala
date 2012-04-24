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
case class SelenateDriverSettings(
    timeout: Long,
    resolution: Long,
    locatorMissingTreshold: Double)


/** Provides defaults, and utility methods for `SelenateDriver`. */
object SelenateDriver {
  /** Default settings for `SelenateDriver` (timeout of 30 s and resolution of 250 ms. */
  val DefaultSettings = SelenateDriverSettings(30000L, 250L, 0.5)

  private def InitFP(fpOpt: Option[FirefoxProfile]): FirefoxProfile =
    fpOpt.getOrElse(new FirefoxProfile())
}



/** Main ''Selenate'' class, based on `FirefoxDriver`. */
class SelenateDriver private(fpOpt: Option[FirefoxProfile], settings: SelenateDriverSettings)
    extends FirefoxDriver(SelenateDriver.InitFP(fpOpt))
    with Capturable {
  import SelenateDriver._

  def this(settings: SelenateDriverSettings = SelenateDriver.DefaultSettings) =
    this(None, settings)
  def this(
      fp: FirefoxProfile,
      settings: SelenateDriverSettings = SelenateDriver.DefaultSettings) =
    this(Some(fp), settings)


  /** Waits for the specified locator to appear.
    *
    * @param locator locator to wait for
    * @throws SelenateException if the locator is never found
    */
  def waitForLocator(locator: Locator) {
    waitOrFail(locator.name) {
      locatorExists(locator)
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
      locatorListExists(locatorList)
    }
  }


  /** Checks weather the specified locator currently exists.
    *
    * @param locator locator to search for
    */
  def locatorExists(locator: Locator): Boolean = {
    val foundElementList = locator.componentSet filter elementExists
    val missingElementList = locator.componentSet &~ foundElementList

    val missingRatio = missingElementList.size.toDouble / locator.componentSet.size.toDouble
    missingRatio < settings.locatorMissingTreshold
  }

  /** Checks weather any locator from the specified list currently exists.
    *
    * @param locatorList a sequence of locators to search for
    */
  def locatorListExists(locatorList: Seq[Locator]): Boolean =
    locatorList map locatorExists _ contains true



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



  protected def elementExists(by: By): Boolean =
    tryFindElement(by).isDefined

  protected def tryFindElement(by: By): Option[WebElement] =
    try {
      Some(findElement(by))
    } catch {
      case e: Exception => None
    }

  protected def waitFor(predicate: => Boolean) =
    util.waitFor(settings.timeout, settings.resolution)
  protected def waitOrFail =
    util.waitOrFail(settings.timeout, settings.resolution)
}

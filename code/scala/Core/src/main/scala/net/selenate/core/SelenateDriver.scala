package net.selenate
package core

import org.openqa.selenium.firefox.{ FirefoxDriver, FirefoxProfile }
import scala.collection.JavaConversions._
import org.openqa.selenium.OutputType
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory


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

  private val logger = LoggerFactory.getLogger(classOf[SelenateDriver])



  /** Checks weather the specified locator currently exists.
    *
    * @param locator locator to search for
    * @return true if the locator exists; false otherwise
    */
  def locatorExists(locator: Locator) = {
    logger.info("Checking existence of locator [%s]..." format locator.toString)
    val result = locatorExistsBase(locator)
    logger.info("Locator [%s] found: %s!".format(locator.toString, result.toString))
    result
  }

  /** Checks weather any locator from the specified set currently exists.
    *
    * @param locatorSet a set of locators to search for
    * @return true if any of the locators exist; false otherwise
    */
  def locatorSetExists(locatorSet: Set[Locator]) = {
    val ps = polite(locatorSet)
    logger.info("Checking existence of locators: %s..." format ps)
    val result = locatorSetExistsAnyBase(locatorSet)
    logger.info("Locators %s found: %s!".format(ps, result.toString))
    result
  }



  /** Waits for the specified locator to appear.
    *
    * @param locator locator to wait for
    * @throws SelenateException if the locator is never found
    */
  def waitForLocator(locator: Locator) {
    logger.info("Waiting for locator: [%s]..." format locator.toString)
    waitOrFail(locator.name) {
      locatorExistsBase(locator)
    }
    logger.info("Locator [%s] found: true!" format locator.toString)
  }

  /** Waits for any locator from the specified set.
    *
    * @param locatorSet a set of locators to wait for
    * @throws SelenateException if none of the locators are ever found
    */
  def waitForLocatorSet(locatorSet: Set[Locator]) {
    val ps = polite(locatorSet)
    logger.info("Waiting for locators %s..." format ps)
    waitOrFail(ps) {
      locatorSetExistsAllBase(locatorSet)
    }
    logger.info("Locators %s found: true!" format ps)
  }



  /** Waits for all locators from the specified page to appear.
    *
    * @param page page whose locators to wait for
    * @throws SelenateException if all of the locators are not found
    */
  def waitForPage(page: Page) {
    logger.info("Waiting for page %s..." format page.toString)
    waitOrFail(page.name) {
      locatorSetExistsAllBase(page.locatorSet)
    }
    logger.info("Page [%s] found: true!" format page.toString)
  }


  /** Waits for any page to appear from the specified list.
    *
    * @param pageList list of pages to wait for
    * @throws SelenateException if none of the pages are found
    */
  def waitForPageList(pageList: Page*) = {
    val ps = polite(pageList)
    logger.info("Waiting for pages %s..." format ps)
    waitOrFail(ps) {
      pageList
        .map(page => locatorSetExistsAllBase(page.locatorSet))
        .fold(false)(_ || _)
    }

    pageList.find(page => locatorSetExistsAllBase(page.locatorSet)).get.name
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

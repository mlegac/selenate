package net.selenate
package core

import org.openqa.selenium.firefox.{ FirefoxDriver, FirefoxProfile }
import scala.collection.JavaConversions._
import org.openqa.selenium.OutputType



/** Storage class for `BaseDriver` settings.
  *
  * @param timeout used when waiting for web page elements to load (maximum waiting time)
  * @param resolution used when waiting for web page elements to load (pause between successive checks)
  */
case class BaseDriverSettings(timeout: Long, resolution: Long)


/** Provides defaults, and utility methods for `BaseDriver`. */
object BaseDriver {
  /** Default settings for `BaseDriver` (timeout of 30 s and resolution of 250 ms. */
  val DefaultSettings = BaseDriverSettings(30000L, 250L)

  private def InitFP(fpOpt: Option[FirefoxProfile]): FirefoxProfile =
    fpOpt.getOrElse(new FirefoxProfile())
}



/** Main ''Selenate'' class, based on `FirefoxDriver`. */
class BaseDriver private(fpOpt: Option[FirefoxProfile], settings: BaseDriverSettings)
    extends FirefoxDriver(BaseDriver.InitFP(fpOpt))
    with Capturable {
  import BaseDriver._

  def this(settings: BaseDriverSettings = BaseDriver.DefaultSettings) =
    this(None, settings)
  def this(
      fp: FirefoxProfile,
      settings: BaseDriverSettings = BaseDriver.DefaultSettings) =
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
    val elementList = locator.componentList.flatMap(findElements(_))
    !elementList.isEmpty
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




  protected def waitFor(predicate: => Boolean) =
    util.waitFor(settings.timeout, settings.resolution)
  protected def waitOrFail =
    util.waitOrFail(settings.timeout, settings.resolution)
}

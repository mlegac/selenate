package net.selenate
package core

import org.openqa.selenium.firefox.{ FirefoxDriver, FirefoxProfile }
import scala.collection.JavaConversions._
import org.openqa.selenium.OutputType
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory


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

  private val logger = LoggerFactory.getLogger(classOf[BaseDriver])

  def locatorExistsBase(locator: Locator): Boolean = {
    logger.debug("Waiting for locator: [%s]..." format locator.toString)
    val foundElementList = locator.componentSet filter elementExists
    val missingElementList = locator.componentSet &~ foundElementList

    logger.trace("Found elements: %s." format polite(foundElementList))
    logger.trace("Missing elements: %s." format polite(missingElementList))
    if (!missingElementList.isEmpty && !foundElementList.isEmpty)
      logger.warn("Locator [%s] has broken element references (%s)".format(locator.toString, polite(missingElementList)))

    val missingRatio = missingElementList.size.toDouble / locator.componentSet.size.toDouble
    logger.trace("Missing ratio: %6.2f%%." format missingRatio*100.0)

    val result = missingRatio < settings.locatorMissingTreshold
    logger.info("Locator [%s] found: %s!".format(locator.toString, result.toString))
    result
  }

  def locatorSetExistsBase(locatorSet: Set[Locator])(op: (Boolean, Boolean) => Boolean): Boolean = {
    val ps = polite(locatorSet)
    logger.info("Checking existence of locators: %s..." format ps)
    val result = locatorSet map locatorExistsBase _ reduceOption op getOrElse(true)
    logger.info("Locators %s found: %s!".format(ps, result.toString))
    result
  }

  def locatorSetExistsAllBase =
    locatorSetExistsBase(_: Set[Locator])(_ && _)
  def locatorSetExistsAnyBase =
    locatorSetExistsBase(_: Set[Locator])(_ || _)


  protected def elementExists(by: By): Boolean =
    tryFindElement(by).isDefined

  protected def tryFindElement(by: By): Option[WebElement] = {
    logger.debug("Trying to find element [%s]..." format by.toString)
    val result = try {
      Some(findElement(by))
    } catch {
      case e: Exception => None
    }
    logger.info("Element [%s] found: %s!".format(by.toString, Opt2TF(result)))
    result
  }


  protected def waitFor =
    util.waitFor(settings.timeout, settings.resolution)
  protected def waitOrFail =
    util.waitOrFail(settings.timeout, settings.resolution)


  protected def Opt2TF[T](opt: Option[T]) =
    opt.map(e => "true").getOrElse("false")

  protected def polite[T](i: Iterable[T]) =
    i.mkString("[", "], [", "]")
}

package net.selenate
package test

import core._

import org.openqa.selenium.By
import org.scalatest._
import matchers._

object BaseDriverTest {
  val TestSettings = BaseDriverSettings(1000, 250)
}
class BaseDriverTest extends FeatureSpec
    with GivenWhenThen
    with MustMatchers {
  import BaseDriverTest._

  info("Initializing BaseDriver with test settings")
  val d = new BaseDriver(TestSettings)

  feature("Element existence testing") {
    info("Loading test page")
    val pageFilename = classOf[BaseDriverTest].getResource("existence.html").getFile
    d.get("file://"+ pageFilename)

    info("Initializig locators")
    val existingLocator = Locator("d1", By.id("d1"))
    val missingLocator1  = Locator("missing1", By.id("missing1"))
    val missingLocator2  = Locator("missing2", By.id("missing2"))

    scenario("verifying that an element does exist") {
      given("an element that does exist")
      val locator = existingLocator
      when("its existence is verified")
      val result = d.elementExists(locator)
      then("it must be found")
      result must equal (true)
    }

    scenario("verifying that en element does not exist") {
      given("an element that does not exist")
      val locator = missingLocator1
      when("its existence is verified")
      val result = d.elementExists(locator)
      then("it must not be found")
      result must equal (false)
    }

    scenario("verifying that any element from a list exists") {
      given("a list of two elements where one does exist and the other does not")
      val locatorList = List(existingLocator, missingLocator1)
      when("their existence is verified")
      val result = d.elementListExists(locatorList)
      then("they must be found")
      result must equal (true)
    }

    scenario("verifying that no element from a list exists") {
      given("a list of two elements where neither exists")
      val locatorList = List(missingLocator1, missingLocator2)
      when("their existence is verified")
      val result = d.elementListExists(locatorList)
      then("they must not be found")
      result must equal (false)
    }

    info("Closing BaseDriver")
    d.close
  }
}
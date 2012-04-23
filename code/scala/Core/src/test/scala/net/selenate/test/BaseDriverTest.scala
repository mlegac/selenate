package net.selenate
package test

import core._

import org.openqa.selenium.By
import org.scalatest._
import matchers._

object BaseDriverTest extends MustMatchers {
  val TestSettings = BaseDriverSettings(5000, 250)

  case class TimedResult[T](r: T, t: Long)
  def exec[T](f: => T): Either[Exception, TimedResult[T]] =
    try {
      val start = System.currentTimeMillis
      val result = f
      val end = System.currentTimeMillis

      Right(TimedResult(result, end-start))
    } catch {
      case e: Exception =>
        e.printStackTrace()
        Left(e)
    }

  def beCloseTo(l: Long) = be >= (l-100) and be <= (l+100)

  def init(name: String): BaseDriver = {
    val d = new BaseDriver(TestSettings)
    val pageFilename = classOf[BaseDriverTest].getResource(name).getFile
    d.get("file://"+ pageFilename)
    d
  }
}



class BaseDriverTest extends FeatureSpec
    with GivenWhenThen
    with MustMatchers
    with EitherValues {
  import BaseDriverTest._

  feature("Element existence verification") {
    info("Initializig locators...")
    val existingLocator = Locator("d1", By.id("d1"))
    val missingLocator1 = Locator("missing1", By.id("missing1"))
    val missingLocator2 = Locator("missing2", By.id("missing2"))

    scenario("verifying that an element does exist") {
      val d = init("existence.html")

      given("an element that does exist")
      val locator = existingLocator
      when("its existence is verified")
      val result = exec(d.locatorExists(locator))
      then("it must be found")
      result.right.value.r must equal (true)

      d.close
    }

    scenario("verifying that en element does not exist") {
      val d = init("existence.html")

      given("an element that does not exist")
      val locator = missingLocator1
      when("its existence is verified")
      val result = exec(d.locatorExists(locator))
      then("it must not be found")
      result.right.value.r must equal (false)

      d.close
    }

    scenario("verifying that any element from a list exists") {
      val d = init("existence.html")

      given("a list of two elements where one does exist and the other does not")
      val locatorList = List(existingLocator, missingLocator1)
      when("their existence is verified")
      val result = exec(d.locatorListExists(locatorList))
      then("they must be found")
      result.right.value.r must equal (true)

      d.close
    }

    scenario("verifying that no element from a list exists") {
      val d = init("existence.html")

      given("a list of two elements where neither exists")
      val locatorList = List(missingLocator1, missingLocator2)
      when("their existence is verified")
      val result = exec(d.locatorListExists(locatorList))
      then("they must not be found")
      result.right.value.r must equal (false)

      d.close
    }
  }



  feature("Waiting for an element to appear") {
    info("Initializig locators...")
    val existingLocator = Locator("d1", By.id("d1"))
    val missingLocator1  = Locator("missing1", By.id("missing1"))
    val missingLocator2  = Locator("missing2", By.id("missing2"))

    scenario("waiting for an element which will appear") {
      val d = init("waiting.html")

      given("an element which will appear")
      val locator = existingLocator
      and("a delay")
      val delay = 500
      and("a timer which will spawn the element after the delay")
      d.executeScript("spawnElementWithDelay('%s', %d);".format("d1", delay))
      when("it is being waited for")
      val result = exec(d.waitForLocator(locator))
      then("it must be found")
      result.right.value.r must equal (())
      and("the waiting time must be close to the delay")
      result.right.value.t must beCloseTo(delay)

      d.close
    }
  }
}
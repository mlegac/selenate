package net.selenate
package test

import core._

import org.openqa.selenium.By
import org.scalatest._
import matchers._

object SelenateDriverTest extends MustMatchers {
  val TestSettings = SelenateSettings(5000, 100, 0.5)

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


  def init(name: String): SelenateDriver = {
    val d = new SelenateDriver(TestSettings)
    val pageFilename = classOf[SelenateDriverTest].getResource(name).getFile
    d.get("file://"+ pageFilename)
    d
  }


  def ≈(i: Interval) = be >= (i.mean-i.precision) and be <= (i.mean+i.precision)
  implicit def impaleInterval(l: Long) = new IntervalImpaler(l)
  class IntervalImpaler(l: Long) { def ±(precision: Long) = Interval(l, precision) }
  case class Interval(mean: Long, precision: Long)
}



class SelenateDriverTest extends FeatureSpec
    with GivenWhenThen
    with MustMatchers
    with EitherValues {
  import SelenateDriverTest._

  feature("Locator existence verification") {
    info("Initializig locators...")
    val locator100 = Locator("d1_100", By.linkText("D1T"), By.id("d1i"), By.className("d1c"), By.xpath("//html[1]/body[1]/a[1]"))
    val locator075 = Locator("d1_075", By.linkText("missing"), By.id("d1i"), By.className("d1c"), By.xpath("//html[1]/body[1]/a[1]"))
    val locator050 = Locator("d1_050", By.linkText("missing"), By.id("missing"), By.className("d1c"), By.xpath("//html[1]/body[1]/a[1]"))
    val locator025 = Locator("d1_025", By.linkText("missing"), By.id("missing"), By.className("missing"), By.xpath("//html[1]/body[1]/a[1]"))
    val locator000 = Locator("d1_000", By.linkText("missing"), By.id("missing"), By.className("missing"), By.xpath("//html[1]/body[1]/missing"))

    scenario("verifying that a locator does exist") {
      val d = init("existence.html")

      given("a locator with missing element ratio above treshold")
      val locator = locator100
      when("its existence is verified")
      val result = exec(d.locatorExists(locator))
      then("it must be found")
      result.right.value.r must equal (true)

      d.close
    }

    scenario("verifying that a locator does not exist") {
      val d = init("existence.html")

      given("a locator with missing element ratio below treshold")
      val locator = locator000
      when("its existence is verified")
      val result = exec(d.locatorExists(locator))
      then("it must not be found")
      result.right.value.r must equal (false)

      d.close
    }

    scenario("verifying that any locator from a list exists") {
      val d = init("existence.html")

      given("a list of two locators where one does exist and the other does not")
      val locatorList = List(locator100, locator000)
      when("their existence is verified")
      val result = exec(d.locatorListExists(locatorList))
      then("they must be found")
      result.right.value.r must equal (true)

      d.close
    }

    scenario("verifying that no locator from a list exists") {
      val d = init("existence.html")

      given("a list of two locators where neither exists")
      val locatorList = List(locator025, locator000)
      when("their existence is verified")
      val result = exec(d.locatorListExists(locatorList))
      then("they must not be found")
      result.right.value.r must equal (false)

      d.close
    }
  }


  feature("Waiting for a locator to appear") {
    info("Initializig locators...")
    val existingLocator = Locator("d1", By.id("d1"))
    val missingLocator1  = Locator("missing1", By.id("missing1"))
    val missingLocator2  = Locator("missing2", By.id("missing2"))

    scenario("waiting for a locator which will appear") {
      val d = init("waiting.html")

      given("a locator which will appear")
      val locator = existingLocator
      and("a delay")
      val delay = 500l
      and("a timer which will spawn the element after the delay")
      d.executeScript("spawnElementWithDelay('%s', %d);".format("d1", delay))
      when("it is being waited for")
      val result = exec(d.waitForLocator(locator))
      then("it must be found")
      result.right.value.r must equal (())
      and("the waiting time must be close to the delay")
      result.right.value.t must ≈ (delay±100)

      d.close
    }
  }
}

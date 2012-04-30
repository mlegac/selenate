package net.selenate
package core

import org.openqa.selenium.By

object Page {
  def apply(name: String, locatorList: Locator*) =
    new Page(name, locatorList.toSet)
}


class Page(val name: String, val locatorSet: Set[Locator]) {
  override def toString = name formatted "@%s"
}

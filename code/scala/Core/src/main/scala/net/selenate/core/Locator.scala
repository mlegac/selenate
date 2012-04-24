package net.selenate
package core

import org.openqa.selenium.By



object Locator {
  def apply(name: String, componentList: By*) =
    new Locator(name, componentList.toSet)
}


class Locator(val name: String, val componentSet: Set[By]) {
  override def toString = name formatted "#%s"
}

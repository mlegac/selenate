package net.selenate

import org.openqa.selenium.By

package object core {
  /** Converts a locator to a representative By value.
    *
    * For now,  returns just the head of locator's component set.
    *
    * @param locator locator to convert
    * @returns a representative By value
    */
  implicit def locator2by(locator: Locator): By =
    locator.componentSet.head
}
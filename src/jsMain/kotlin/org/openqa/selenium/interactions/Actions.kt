package org.openqa.selenium.interactions

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.internal.BuiltActions

class Actions(driver: WebDriver) {
    fun moveToElement(element: WebElement, offsetX: Int, offsetY: Int): Actions = TODO()
    fun click(): Actions = TODO()
    fun build(): BuiltActions = TODO()
    fun perform() = build().perform()
}

package org.openqa.selenium.interactions

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.internal.BuiltActions

class Actions(driver: WebDriver) {
    fun moveToElement(element: WebElement, offsetX: Int = 0, offsetY: Int = 0): Actions = TODO()
    fun click(): Actions = TODO()
    fun contextClick(element: WebElement): Actions = TODO()
    fun doubleClick(element: WebElement): Actions = TODO()
    fun dragAndDrop(from: WebElement, target: WebElement): Actions = TODO("Not yet implemented")

    fun build(): BuiltActions = TODO()
    fun perform() = build().perform()
}

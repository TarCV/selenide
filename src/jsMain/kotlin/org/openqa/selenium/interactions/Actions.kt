package org.openqa.selenium.interactions

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.internal.BuiltActions

actual class Actions actual constructor(driver: WebDriver) {
    actual fun moveToElement(element: WebElement, offsetX: Int, offsetY: Int): Actions = TODO()
    actual fun moveToElement(element: WebElement): Actions = TODO()
    actual fun click(): Actions = TODO()
    actual fun contextClick(element: WebElement): Actions = TODO()
    actual fun doubleClick(element: WebElement): Actions = TODO()
    actual fun dragAndDrop(from: WebElement, target: WebElement): Actions = TODO("Not yet implemented")

    actual fun build(): BuiltActions = TODO()
    actual fun perform() = build().perform()
}

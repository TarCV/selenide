package org.openqa.selenium.interactions

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.internal.BuiltActions
import org.openqa.selenium.interactions.Actions

actual class Actions actual constructor(driver: org.openqa.selenium.WebDriver) {
    actual fun moveToElement(element: org.openqa.selenium.WebElement, offsetX: Int, offsetY: Int): org.openqa.selenium.interactions.Actions = TODO()
    actual fun click(): org.openqa.selenium.interactions.Actions = TODO()
    actual fun contextClick(element: org.openqa.selenium.WebElement): org.openqa.selenium.interactions.Actions = TODO()
    actual fun doubleClick(element: org.openqa.selenium.WebElement): org.openqa.selenium.interactions.Actions = TODO()
    actual fun dragAndDrop(from: org.openqa.selenium.WebElement, target: org.openqa.selenium.WebElement): org.openqa.selenium.interactions.Actions = TODO("Not yet implemented")

    actual fun build(): BuiltActions = TODO()
    actual fun perform() = build().perform()
}

package org.openqa.selenium.interactions

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.internal.BuiltActions

expect class Actions constructor(driver: WebDriver) {
    fun moveToElement(element: org.openqa.selenium.WebElement, offsetX: Int, offsetY: Int): Actions
    fun click(): Actions
    fun contextClick(element: org.openqa.selenium.WebElement): Actions
    fun doubleClick(element: org.openqa.selenium.WebElement): Actions
    fun dragAndDrop(from: org.openqa.selenium.WebElement, target: org.openqa.selenium.WebElement): Actions

    fun build(): BuiltActions
    fun perform()
    fun moveToElement(element: WebElement): Actions
}

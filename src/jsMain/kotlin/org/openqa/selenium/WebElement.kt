package org.openqa.selenium

import org.openqa.selenium.Keys
import org.openqa.selenium.Location
import org.openqa.selenium.SearchContext

actual interface WebElement: org.openqa.selenium.SearchContext {
    actual val isDisplayed: Boolean
    actual val isEnabled: Boolean
    actual val isSelected: Boolean
    actual val text: String
    actual val tagName: String

    actual suspend fun clear()
    actual suspend fun click()
    actual suspend fun getAttribute(name: String): String?
    actual suspend fun getCssValue(propertyName: String): String
    actual suspend fun getLocation(): org.openqa.selenium.Location
    actual suspend fun sendKeys(keys: String)
    actual suspend fun sendKeys(key: org.openqa.selenium.Keys)
}

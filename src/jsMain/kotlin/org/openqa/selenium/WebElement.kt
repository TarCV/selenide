package org.openqa.selenium

import org.openqa.selenium.Keys
import org.openqa.selenium.Location
import org.openqa.selenium.SearchContext

actual interface WebElement: org.openqa.selenium.SearchContext {
    actual suspend fun isDisplayed(): Boolean
    actual suspend fun isEnabled(): Boolean
    actual suspend fun isSelected(): Boolean
    actual suspend fun getText(): String
    actual suspend fun getTagName(): String

    actual suspend fun clear()
    actual suspend fun click()
    actual suspend fun getAttribute(name: String): String?
    actual suspend fun getCssValue(propertyName: String): String
    actual suspend fun getLocation(): org.openqa.selenium.Location
    actual suspend fun sendKeys(keys: String)
    actual suspend fun sendKeys(key: org.openqa.selenium.Keys)
}

package org.openqa.selenium.support.ui

import org.openqa.selenium.Alert
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

object ExpectedConditions {
    fun frameToBeAvailableAndSwitchToIt(index: Int): ExpectedCondition<WebDriver> = TODO()
    fun frameToBeAvailableAndSwitchToIt(id: String): ExpectedCondition<WebDriver> = TODO()
    fun frameToBeAvailableAndSwitchToIt(frame: WebElement): ExpectedCondition<WebDriver> = TODO()
    fun alertIsPresent(): ExpectedCondition<Alert> = TODO()
}

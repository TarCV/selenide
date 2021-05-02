package org.openqa.selenium.support.ui

import org.openqa.selenium.Alert
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition

actual object ExpectedConditions {
    actual fun frameToBeAvailableAndSwitchToIt(index: Int): ExpectedCondition<org.openqa.selenium.WebDriver> = TODO()
    actual fun frameToBeAvailableAndSwitchToIt(id: String): ExpectedCondition<org.openqa.selenium.WebDriver> = TODO()
    actual fun frameToBeAvailableAndSwitchToIt(frame: org.openqa.selenium.WebElement): ExpectedCondition<org.openqa.selenium.WebDriver> = TODO()
    actual fun alertIsPresent(): ExpectedCondition<org.openqa.selenium.Alert> = TODO()
}

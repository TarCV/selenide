package com.codeborne.selenide.impl.windows

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.ExpectedCondition

/**
 * A slightly fixed implementation of [ExpectedConditions.frameToBeAvailableAndSwitchToIt]
 */
class FrameByIdOrName(frame: String?) : ExpectedCondition<org.openqa.selenium.WebDriver> {
    private val locator: org.openqa.selenium.By =
      org.openqa.selenium.By.cssSelector("frame#${frame},frame[name=${frame}],iframe#${frame},iframe[name=${frame}]")

  override fun apply(driver: org.openqa.selenium.WebDriver?): org.openqa.selenium.WebDriver? {
        return try {
            checkNotNull(driver).switchTo().frame(driver.findElement(locator))
        } catch (e: org.openqa.selenium.WebDriverException) {
            null
        }
    }
    override fun toString(): String {
        return "frame to be available: $locator"
    }

}

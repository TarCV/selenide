package com.codeborne.selenide.impl.windows

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.ExpectedCondition

/**
 * A slightly fixed implementation of [ExpectedConditions.frameToBeAvailableAndSwitchToIt]
 */
class FrameByIdOrName(frame: String?) : ExpectedCondition<WebDriver> {
    private val locator: By =
      By.cssSelector("frame#${frame}\$s,frame[name=${frame}\$s],iframe#${frame}\$s,iframe[name=${frame}\$s]")

  override suspend operator fun invoke(driver: WebDriver): WebDriver? {
        return try {
            checkNotNull(driver).switchTo().frame(driver.findElement(locator))
        } catch (e: WebDriverException) {
            null
        }
    }
    override fun toString(): String {
        return "frame to be available: $locator"
    }

}

package com.codeborne.selenide.impl.windows

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.ExpectedCondition
import java.util.Objects
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

/**
 * A slightly fixed implementation of [ExpectedConditions.frameToBeAvailableAndSwitchToIt]
 */
@ParametersAreNonnullByDefault
class FrameByIdOrName(frame: String?) : ExpectedCondition<WebDriver> {
    private val locator: By =
      By.cssSelector(String.format("frame#%1\$s,frame[name=%1\$s],iframe#%1\$s,iframe[name=%1\$s]", frame))

  override fun apply(driver: WebDriver?): WebDriver? {
        return try {
            checkNotNull(driver).switchTo().frame(driver.findElement(locator))
        } catch (e: WebDriverException) {
            null
        }
    }

    @CheckReturnValue
    override fun toString(): String {
        return "frame to be available: $locator"
    }

}

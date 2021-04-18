package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.impl.DurationFormat
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
object ErrorMessages {
    private val df = DurationFormat()
    @JvmStatic
    @CheckReturnValue
    fun timeout(timeoutMs: Long): String {
        return String.format("%nTimeout: %s", df.format(timeoutMs))
    }

    @CheckReturnValue
    fun actualValue(condition: Condition, driver: Driver, element: WebElement?): String {
        if (element != null) {
            try {
                val actualValue = condition.actualValue(driver, element)
                if (actualValue != null) {
                    return String.format("%nActual value: %s", actualValue)
                }
            } catch (failedToGetValue: RuntimeException) {
                val failedActualValue = failedToGetValue.javaClass.simpleName + ": " + failedToGetValue.message
                return String.format("%nActual value: %s", StringUtils.substring(failedActualValue, 0, 50))
            }
        }
        return ""
    }

    @CheckReturnValue
    fun causedBy(cause: Throwable?): String {
        if (cause == null) {
            return ""
        }
        return if (cause is WebDriverException) {
            String.format("%nCaused by: %s", Cleanup.of.webdriverExceptionMessage(cause))
        } else String.format("%nCaused by: %s", cause)
    }

    @CheckReturnValue
    private fun getHtmlFilePath(screenshotPath: String): String {
        return screenshotPath.substring(0, screenshotPath.lastIndexOf('.')) + ".html"
    }
}

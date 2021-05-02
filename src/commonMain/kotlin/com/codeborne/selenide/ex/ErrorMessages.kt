package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.impl.DurationFormat
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement

object ErrorMessages {
    private val df = DurationFormat()
    fun timeout(timeoutMs: Long): String {
        return "\nTimeout: ${df.format(timeoutMs)}"
    }
    suspend fun actualValue(condition: Condition, driver: Driver, element: org.openqa.selenium.WebElement?): String {
        if (element != null) {
            try {
                val actualValue = condition.actualValue(driver, element)
                if (actualValue != null) {
                    return "\nActual value: ${actualValue}"
                }
            } catch (failedToGetValue: RuntimeException) {
                val failedActualValue = failedToGetValue::class.simpleName + ": " + failedToGetValue.message
                return "\nActual value: ${failedActualValue.substring(0, 50)}"
            }
        }
        return ""
    }
    fun causedBy(cause: Throwable?): String {
        if (cause == null) {
            return ""
        }
        return if (cause is org.openqa.selenium.WebDriverException) {
            "\nCaused by: ${Cleanup.of.webdriverExceptionMessage(cause)}"
        } else "\nCaused by: ${cause}"
    }
    private fun getHtmlFilePath(screenshotPath: String): String {
        return screenshotPath.substring(0, screenshotPath.lastIndexOf('.')) + ".html"
    }
}

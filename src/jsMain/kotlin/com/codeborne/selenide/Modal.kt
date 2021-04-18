package com.codeborne.selenide

import com.codeborne.selenide.ex.DialogTextMismatch
import com.codeborne.selenide.ex.UIAssertionError
import com.codeborne.selenide.logevents.SelenideLogger

class Modal(private val driver: Driver) {
    @kotlin.time.ExperimentalTime
    suspend fun confirm(expectedDialogText: String? = null): String {
        return SelenideLogger.getAsync("confirm", (expectedDialogText ?: "")) {
            val alert = driver.switchTo().alert()
            val actualDialogText = alert.text
            alert.accept()
            checkDialogText(driver, expectedDialogText, actualDialogText)
            actualDialogText
        }
    }

    @kotlin.time.ExperimentalTime
    suspend fun prompt(expectedDialogText: String? = null, inputText: String? = null): String {
        val description = (inputText ?: "") + " -> " + (expectedDialogText ?: "")
        return SelenideLogger.getAsync("prompt", description) {
            val alert = driver.switchTo().alert()
            val actualDialogText = alert.text
            if (inputText != null) {
                alert.sendKeys(inputText)
            }
            alert.accept()
            checkDialogText(driver, expectedDialogText, actualDialogText)
            actualDialogText
        }
    }

    @kotlin.time.ExperimentalTime
    suspend fun dismiss(expectedDialogText: String? = null): String {
        return SelenideLogger.getAsync("dismiss", (expectedDialogText ?: "")) {
            val alert = driver.switchTo().alert()
            val actualDialogText = alert.text
            alert.dismiss()
            checkDialogText(driver, expectedDialogText, actualDialogText)
            actualDialogText
        }
    }

    companion object {
        private fun checkDialogText(driver: Driver, expectedDialogText: String?, actualDialogText: String) {
            if (expectedDialogText != null && expectedDialogText != actualDialogText) {
                val assertionError = DialogTextMismatch(driver, actualDialogText, expectedDialogText)
                throw UIAssertionError.wrap(driver, assertionError, driver.config().timeout())
            }
        }
    }
}

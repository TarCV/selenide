package com.codeborne.selenide

import com.codeborne.selenide.ex.DialogTextMismatch
import com.codeborne.selenide.ex.UIAssertionError
import com.codeborne.selenide.logevents.SelenideLogger
import org.apache.commons.lang3.StringUtils
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Modal(private val driver: Driver) {
    @JvmOverloads
    fun confirm(expectedDialogText: String? = null): String {
        return SelenideLogger.get("confirm", StringUtils.defaultString(expectedDialogText)) {
            val alert = driver.switchTo().alert()
            val actualDialogText = alert.text
            alert.accept()
            checkDialogText(driver, expectedDialogText, actualDialogText)
            actualDialogText
        }
    }

    @JvmOverloads
    fun prompt(expectedDialogText: String? = null, inputText: String? = null): String {
        val description = StringUtils.defaultString(inputText) + " -> " + StringUtils.defaultString(expectedDialogText)
        return SelenideLogger.get("prompt", description) {
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

    @JvmOverloads
    fun dismiss(expectedDialogText: String? = null): String {
        return SelenideLogger.get("dismiss", StringUtils.defaultString(expectedDialogText)) {
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

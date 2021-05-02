package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.ex.ElementIsNotClickableException
import com.codeborne.selenide.ex.InvalidStateException
import com.codeborne.selenide.ex.UIAssertionError
import org.openqa.selenium.InvalidElementStateException
import org.openqa.selenium.NotFoundException
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriverException

internal class ExceptionWrapper {
    suspend fun wrap(lastError: Throwable, webElementSource: WebElementSource): Throwable {
        if (lastError is UIAssertionError) {
            return lastError
        } else if (lastError is org.openqa.selenium.InvalidElementStateException) {
            return InvalidStateException(webElementSource.driver(), lastError)
        } else if (isElementNotClickableException(lastError)) {
            return ElementIsNotClickableException(webElementSource.driver(), lastError)
        } else if (lastError is org.openqa.selenium.StaleElementReferenceException || lastError is org.openqa.selenium.NotFoundException) {
            return webElementSource.createElementNotFoundError(Condition.exist, lastError)
        }
        return lastError
    }
    private fun isElementNotClickableException(e: Throwable): Boolean {
        return e is org.openqa.selenium.WebDriverException && e.message?.contains("is not clickable") == true
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.ex.ElementIsNotClickableException
import com.codeborne.selenide.ex.InvalidStateException
import com.codeborne.selenide.ex.UIAssertionError
import org.openqa.selenium.InvalidElementStateException
import org.openqa.selenium.NotFoundException
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriverException
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
internal class ExceptionWrapper {
    @CheckReturnValue
    fun wrap(lastError: Throwable, webElementSource: WebElementSource): Throwable {
        if (lastError is UIAssertionError) {
            return lastError
        } else if (lastError is InvalidElementStateException) {
            return InvalidStateException(webElementSource.driver(), lastError)
        } else if (isElementNotClickableException(lastError)) {
            return ElementIsNotClickableException(webElementSource.driver(), lastError)
        } else if (lastError is StaleElementReferenceException || lastError is NotFoundException) {
            return webElementSource.createElementNotFoundError(Condition.exist, lastError)
        }
        return lastError
    }

    @CheckReturnValue
    private fun isElementNotClickableException(e: Throwable): Boolean {
        return e is WebDriverException && e.message!!.contains("is not clickable")
    }
}

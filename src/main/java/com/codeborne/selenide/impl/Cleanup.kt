package com.codeborne.selenide.impl

import org.openqa.selenium.InvalidSelectorException
import java.util.Objects
import java.util.regex.Pattern
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Cleanup {
    @CheckReturnValue
    fun webdriverExceptionMessage(webDriverException: Throwable): String {
        return checkNotNull(webdriverExceptionMessage(webDriverException.toString()))
    }

    @CheckReturnValue
    fun webdriverExceptionMessage(webDriverExceptionInfo: String?): String? {
        return if (webDriverExceptionInfo == null) null else cleanupSeleniumPackage(
            cleanupSeleniumWarning(
                extractFirstLine(
                    webDriverExceptionInfo
                )
            )
        ).trim { it <= ' ' }
    }

    private fun extractFirstLine(text: String): String {
        return REGEX_FIRST_LINE.matcher(text).replaceFirst("$1")
    }

    private fun cleanupSeleniumWarning(firstLine: String): String {
        return REGEX_SELENIUM_WARNING.matcher(firstLine).replaceFirst("$1")
    }

    private fun cleanupSeleniumPackage(withoutSeleniumBloat: String): String {
        return REGEX_SELENIUM_PACKAGE.matcher(withoutSeleniumBloat).replaceFirst("$1")
    }

    fun isInvalidSelectorError(error: Throwable?): Boolean {
        if (error == null) return false
        if (error is AssertionError) return false
        val message = error.message ?: return false
        return error is InvalidSelectorException && !message.contains("\"Element is not selectable\"") ||
                isInvalidSelectorMessage(message) || error.cause != null && error.cause !== error && isInvalidSelectorError(
            error.cause
        )
    }

    private fun isInvalidSelectorMessage(message: String): Boolean {
        return message.contains("invalid or illegal string was specified") ||
                message.contains("Invalid selector") ||
                message.contains("invalid selector") ||
                message.contains("is not a valid selector") ||
                message.contains("SYNTAX_ERR") ||
                message.contains("INVALID_EXPRESSION_ERR")
    }

    fun wrap(error: Throwable?): InvalidSelectorException {
        return if (error is InvalidSelectorException) error else InvalidSelectorException("Invalid selector", error)
    }

    companion object {
        private val REGEX_FIRST_LINE = Pattern.compile("([^\\n]*)\\n.*", Pattern.DOTALL)
        private val REGEX_SELENIUM_WARNING =
            Pattern.compile("(.*)\\(WARNING: The server did not provide any stacktrace.*")
        private val REGEX_SELENIUM_PACKAGE = Pattern.compile("org\\.openqa\\.selenium\\.(.*)")
        @JvmField
        var of = Cleanup()
    }
}

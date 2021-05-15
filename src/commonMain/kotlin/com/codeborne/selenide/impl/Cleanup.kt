package com.codeborne.selenide.impl

import org.openqa.selenium.InvalidSelectorException
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class Cleanup {
    fun webdriverExceptionMessage(webDriverException: Throwable): String {
        return checkNotNull(webdriverExceptionMessage(webDriverException.toString()))
    }
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
        return REGEX_FIRST_LINE.replaceFirst(text, "$1")
    }

    private fun cleanupSeleniumWarning(firstLine: String): String {
        return REGEX_SELENIUM_WARNING.replaceFirst(firstLine, "$1")
    }

    private fun cleanupSeleniumPackage(withoutSeleniumBloat: String): String {
        return REGEX_SELENIUM_PACKAGE.replaceFirst(withoutSeleniumBloat, "$1")
    }

    fun isInvalidSelectorError(error: Throwable?): Boolean {
        if (error == null) return false
        if (error is AssertionError) return false
        val message = error.message ?: return false
        return error is org.openqa.selenium.InvalidSelectorException && !message.contains("\"Element is not selectable\"") ||
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

    fun wrap(error: Throwable?): org.openqa.selenium.InvalidSelectorException {
        return if (error is org.openqa.selenium.InvalidSelectorException) error else org.openqa.selenium.InvalidSelectorException(
            "Invalid selector",
            error
        )
    }

    companion object {
        private val REGEX_FIRST_LINE = Regex("([^\\n]*)\\n.*", RegexOption.MULTILINE)
        private val REGEX_SELENIUM_WARNING =
            Regex("(.*)\\(WARNING: The server did not provide any stacktrace.*")
        private val REGEX_SELENIUM_PACKAGE = Regex("org\\.openqa\\.selenium\\.(.*)")

        @JvmField
        val of = Cleanup()
    }
}

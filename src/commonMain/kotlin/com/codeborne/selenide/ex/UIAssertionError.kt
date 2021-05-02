package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Cleanup
import org.openqa.selenium.WebDriverException

open class UIAssertionError : AssertionError {
    private val driver: Driver

    /**
     * Get path to screenshot taken after failed test
     *
     * @return empty string if screenshots are disabled
     */
    var screenshot = "" // TODO none()
        private set
    var timeoutMs: Long = 0

    protected constructor(driver: Driver, message: String?) : super(message) {
        this.driver = driver
    }

    constructor(driver: Driver, message: String?, cause: Throwable?) : super(message, cause) {
        this.driver = driver
    }
    override val message: String
        get() = super.message + uiDetails()
    override fun toString(): String {
        return message
    }
    protected fun uiDetails(): String {
        return /*TODO: screenshot.summary() +*/ ErrorMessages.timeout(timeoutMs) + ErrorMessages.causedBy(cause)
    }

    companion object {
        fun wrap(driver: Driver, error: Error, timeoutMs: Long): Error {
            return if (Cleanup.of.isInvalidSelectorError(error)) error else wrapThrowable(driver, error, timeoutMs)
        }
        fun wrap(driver: Driver, error: org.openqa.selenium.WebDriverException, timeoutMs: Long): Throwable {
            return if (Cleanup.of.isInvalidSelectorError(error)) error else wrapThrowable(driver, error, timeoutMs)
        }
        private fun wrapThrowable(driver: Driver, error: Throwable, timeoutMs: Long): UIAssertionError {
            val uiError = if (error is UIAssertionError) error else wrapToUIAssertionError(driver, error)
            uiError.timeoutMs = timeoutMs
// TODO:            uiError.screenshot = instance.takeScreenshot(driver)
            return uiError
        }
        private fun wrapToUIAssertionError(driver: Driver, error: Throwable): UIAssertionError {
            val message = error::class.simpleName + ": " + Cleanup.of.webdriverExceptionMessage(error.message)
            return UIAssertionError(driver, message, error)
        }
    }
}

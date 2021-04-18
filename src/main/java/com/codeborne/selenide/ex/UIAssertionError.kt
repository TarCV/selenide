package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.impl.ScreenShotLaboratory.Companion.instance
import com.codeborne.selenide.impl.Screenshot.Companion.none
import org.openqa.selenium.WebDriverException
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class UIAssertionError : AssertionError {
    private val driver: Driver

    /**
     * Get path to screenshot taken after failed test
     *
     * @return empty string if screenshots are disabled
     */
    @get:CheckReturnValue
    var screenshot = none()
        private set
    @JvmField
    var timeoutMs: Long = 0

    protected constructor(driver: Driver, message: String?) : super(message) {
        this.driver = driver
    }

    constructor(driver: Driver, message: String?, cause: Throwable?) : super(message, cause) {
        this.driver = driver
    }

    @get:CheckReturnValue
    override val message: String
        get() = super.message + uiDetails()

    @CheckReturnValue
    override fun toString(): String {
        return message
    }

    @CheckReturnValue
    protected fun uiDetails(): String {
        return screenshot.summary() + ErrorMessages.timeout(timeoutMs) + ErrorMessages.causedBy(cause)
    }

    companion object {
        @CheckReturnValue
        fun wrap(driver: Driver, error: Error, timeoutMs: Long): Error {
            return if (Cleanup.of.isInvalidSelectorError(error)) error else wrapThrowable(driver, error, timeoutMs)
        }

        @CheckReturnValue
        fun wrap(driver: Driver, error: WebDriverException, timeoutMs: Long): Throwable {
            return if (Cleanup.of.isInvalidSelectorError(error)) error else wrapThrowable(driver, error, timeoutMs)
        }

        @CheckReturnValue
        private fun wrapThrowable(driver: Driver, error: Throwable, timeoutMs: Long): UIAssertionError {
            val uiError = if (error is UIAssertionError) error else wrapToUIAssertionError(driver, error)
            uiError.timeoutMs = timeoutMs
            uiError.screenshot = instance.takeScreenshot(driver)
            return uiError
        }

        @CheckReturnValue
        private fun wrapToUIAssertionError(driver: Driver, error: Throwable): UIAssertionError {
            val message = error.javaClass.simpleName + ": " + Cleanup.of.webdriverExceptionMessage(error.message)
            return UIAssertionError(driver, message, error)
        }
    }
}

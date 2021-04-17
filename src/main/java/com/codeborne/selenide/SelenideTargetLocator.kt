package com.codeborne.selenide

import com.codeborne.selenide.ex.AlertNotFoundException
import com.codeborne.selenide.ex.FrameNotFoundException
import com.codeborne.selenide.ex.UIAssertionError
import com.codeborne.selenide.ex.WindowNotFoundException
import com.codeborne.selenide.impl.windows.FrameByIdOrName
import com.codeborne.selenide.impl.windows.WindowByIndex
import com.codeborne.selenide.impl.windows.WindowByNameOrHandle
import org.openqa.selenium.Alert
import org.openqa.selenium.InvalidArgumentException
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriver.TargetLocator
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SelenideTargetLocator(private val driver: Driver) : TargetLocator {
    private val webDriver: WebDriver = driver.webDriver
    private val config: Config = driver.config()
    private val delegate: TargetLocator = webDriver.switchTo()

    override fun frame(index: Int): WebDriver {
        return try {
            Wait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index))
        } catch (e: NoSuchElementException) {
            throw frameNotFoundError("No frame found with index: $index", e)
        } catch (e: TimeoutException) {
            throw frameNotFoundError("No frame found with index: $index", e)
        } catch (e: InvalidArgumentException) {
            if (isFirefox62Bug(e) || isChrome75Error(e)) {
                throw frameNotFoundError("No frame found with index: $index", e)
            }
            throw e
        }
    }

    override fun frame(nameOrId: String): WebDriver {
        return try {
            Wait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId))
        } catch (e: NoSuchElementException) {
            throw frameNotFoundError("No frame found with id/name: $nameOrId", e)
        } catch (e: TimeoutException) {
            throw frameNotFoundError("No frame found with id/name: $nameOrId", e)
        } catch (e: InvalidArgumentException) {
            if (isFirefox62Bug(e)) {
                throw frameNotFoundError("No frame found with id/name: $nameOrId", e)
            }
            throw e
        }
    }

    override fun frame(frameElement: WebElement): WebDriver {
        return try {
            Wait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement))
        } catch (e: NoSuchElementException) {
            throw frameNotFoundError("No frame found with element: $frameElement", e)
        } catch (e: TimeoutException) {
            throw frameNotFoundError("No frame found with element: $frameElement", e)
        } catch (e: InvalidArgumentException) {
            if (isFirefox62Bug(e)) {
                throw frameNotFoundError("No frame found with element: $frameElement", e)
            }
            throw e
        }
    }

    private fun isFirefox62Bug(e: InvalidArgumentException): Boolean {
        return e.message!!.contains("untagged enum FrameId")
    }

    private fun isChrome75Error(e: InvalidArgumentException): Boolean {
        return e.message!!.contains("invalid argument: 'id' out of range")
    }

    override fun parentFrame(): WebDriver {
        return delegate.parentFrame()
    }

    override fun defaultContent(): WebDriver {
        return delegate.defaultContent()
    }

    override fun activeElement(): WebElement {
        return delegate.activeElement()
    }

    override fun alert(): Alert {
        return try {
            Wait().until(ExpectedConditions.alertIsPresent())
        } catch (e: TimeoutException) {
            throw alertNotFoundError(e)
        }
    }

    /**
     * Switch to the inner frame (last child frame in given sequence)
     */
    fun innerFrame(vararg frames: String): WebDriver {
        delegate.defaultContent()
        for (frame in frames) {
            try {
                Wait().until(FrameByIdOrName(frame))
            } catch (e: NoSuchElementException) {
                throw frameNotFoundError("No frame found with id/name = $frame", e)
            } catch (e: TimeoutException) {
                throw frameNotFoundError("No frame found with id/name = $frame", e)
            }
        }
        return webDriver
    }

    /**
     * Switch to window/tab by index
     * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
     *
     * @param index index of window (0-based)
     */
    fun window(index: Int): WebDriver {
        return try {
            Wait().until(WindowByIndex(index))
        } catch (e: TimeoutException) {
            throw windowNotFoundError("No window found with index: $index", e)
        }
    }

    /**
     * Switch to window/tab by index with a configurable timeout
     * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
     *
     * @param index    index of window (0-based)
     * @param duration the timeout duration. It overrides default Config.timeout()
     */
    fun window(index: Int, duration: Duration): WebDriver {
        return try {
            Wait(duration).until(WindowByIndex(index))
        } catch (e: TimeoutException) {
            throw windowNotFoundError("No window found with index: $index", e)
        }
    }

    /**
     * Switch to window/tab by name/handle/title
     *
     * @param nameOrHandleOrTitle name or handle or title of window/tab
     */
    override fun window(nameOrHandleOrTitle: String): WebDriver {
        return try {
            Wait().until(WindowByNameOrHandle(nameOrHandleOrTitle))
        } catch (e: TimeoutException) {
            throw windowNotFoundError("No window found with name or handle or title: $nameOrHandleOrTitle", e)
        }
    }

    /**
     * Switch to window/tab by name/handle/title with a configurable timeout
     *
     * @param nameOrHandleOrTitle name or handle or title of window/tab
     * @param duration            the timeout duration. It overrides default Config.timeout()
     */
    fun window(nameOrHandleOrTitle: String, duration: Duration): WebDriver {
        return try {
            Wait(duration).until(WindowByNameOrHandle(nameOrHandleOrTitle))
        } catch (e: TimeoutException) {
            throw windowNotFoundError("No window found with name or handle or title: $nameOrHandleOrTitle", e)
        }
    }

    private fun Wait(): SelenideWait {
        return SelenideWait(webDriver, config.timeout(), config.pollingInterval())
    }

    private fun Wait(timeout: Duration): SelenideWait {
        return SelenideWait(webDriver, timeout.toMillis(), config.pollingInterval())
    }

    private fun frameNotFoundError(message: String, cause: Throwable): Error {
        val error = FrameNotFoundException(driver, message, cause)
        return UIAssertionError.wrap(driver, error, config.timeout())
    }

    private fun windowNotFoundError(message: String, cause: Throwable): Error {
        val error = WindowNotFoundException(driver, message, cause)
        return UIAssertionError.wrap(driver, error, config.timeout())
    }

    private fun alertNotFoundError(cause: Throwable): Error {
        val error = AlertNotFoundException(driver, "Alert not found", cause)
        return UIAssertionError.wrap(driver, error, config.timeout())
    }

}

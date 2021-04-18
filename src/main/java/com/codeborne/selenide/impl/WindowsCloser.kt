package com.codeborne.selenide.impl

import com.google.errorprone.annotations.CheckReturnValue
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class WindowsCloser {
    @CheckReturnValue
    @Throws(FileNotFoundException::class)
    open fun <T: Any> runAndCloseArisedWindows(webDriver: WebDriver, lambda: SupplierWithException<T>): T {
        val originalWindowHandle = webDriver.windowHandle
        val windowsBefore = webDriver.windowHandles
        return try {
            lambda.get()
        } finally {
            closeArisedWindows(webDriver, originalWindowHandle, windowsBefore)
        }
    }

    private fun closeArisedWindows(webDriver: WebDriver, originalWindowHandle: String, windowsBefore: Set<String>) {
        val newWindows = newWindows(webDriver, windowsBefore)
        if (newWindows.isNotEmpty()) {
            closeWindows(webDriver, newWindows)
            webDriver.switchTo().window(originalWindowHandle)
        }
    }

    @CheckReturnValue
    private fun newWindows(webDriver: WebDriver, windowsBefore: Set<String>): Set<String> {
        val windowHandles = webDriver.windowHandles
        val newWindows: MutableSet<String> = HashSet(windowHandles)
        newWindows.removeAll(windowsBefore)
        return newWindows
    }

    private fun closeWindows(webDriver: WebDriver, windows: Set<String>) {
        log.info("File has been opened in a new window, let's close {} new windows", windows.size)
        for (newWindow in windows) {
            closeWindow(webDriver, newWindow)
        }
    }

    private fun closeWindow(webDriver: WebDriver, window: String) {
        log.info("  Let's close {}", window)
        try {
            webDriver.switchTo().window(window)
            webDriver.close()
        } catch (windowHasBeenClosedMeanwhile: NoSuchWindowException) {
            log.info(
                "  Failed to close {}: {}",
                window,
                Cleanup.of.webdriverExceptionMessage(windowHasBeenClosedMeanwhile)
            )
        } catch (e: Exception) {
            log.warn("  Failed to close {}", window, e)
        }
    }

    fun interface SupplierWithException<T> {
        @Throws(FileNotFoundException::class)
        fun get(): T
    }

    companion object {
        private val log = LoggerFactory.getLogger(WindowsCloser::class.java)
    }
}

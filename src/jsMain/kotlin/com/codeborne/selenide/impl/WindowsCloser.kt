package com.codeborne.selenide.impl

import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import okio.okio.FileNotFoundException

open class WindowsCloser {
    open suspend fun <T: Any> runAndCloseArisedWindows(webDriver: WebDriver, lambda: SupplierWithException<T>): T {
        val originalWindowHandle = webDriver.windowHandle
        val windowsBefore = webDriver.windowHandles
        return try {
            lambda.get()
        } finally {
            closeArisedWindows(webDriver, originalWindowHandle, windowsBefore)
        }
    }

    private suspend fun closeArisedWindows(webDriver: WebDriver, originalWindowHandle: String, windowsBefore: Set<String>) {
        val newWindows = newWindows(webDriver, windowsBefore)
        if (newWindows.isNotEmpty()) {
            closeWindows(webDriver, newWindows)
            webDriver.switchTo().window(originalWindowHandle)
        }
    }
    private fun newWindows(webDriver: WebDriver, windowsBefore: Set<String>): Set<String> {
        val windowHandles = webDriver.windowHandles
        val newWindows: MutableSet<String> = HashSet(windowHandles)
        newWindows.removeAll(windowsBefore)
        return newWindows
    }

    private suspend fun closeWindows(webDriver: WebDriver, windows: Set<String>) {
        log.info("File has been opened in a new window, let's close {} new windows", windows.size)
        for (newWindow in windows) {
            closeWindow(webDriver, newWindow)
        }
    }

    private suspend fun closeWindow(webDriver: WebDriver, window: String) {
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
        fun get(): T
    }

    companion object {
        private val log = LoggerFactory.getLogger(WindowsCloser::class)
    }
}

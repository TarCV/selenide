package com.codeborne.selenide.impl

import org.lighthousegames.logging.logging
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver

open class WindowsCloser {
    open suspend fun <T: Any> runAndCloseArisedWindows(webDriver: WebDriver, lambda: suspend () -> T): T {
        val originalWindowHandle = webDriver.windowHandle
        val windowsBefore = webDriver.windowHandles
        return try {
            lambda.invoke()
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
        log.info { "Path has been opened in a new window, let's close ${windows.size} new windows" }
        for (newWindow in windows) {
            closeWindow(webDriver, newWindow)
        }
    }

    private suspend fun closeWindow(webDriver: WebDriver, window: String) {
        log.info { "  Let's close $window" }
        try {
            webDriver.switchTo().window(window)
            webDriver.close()
        } catch (windowHasBeenClosedMeanwhile: NoSuchWindowException) {
            log.info { "  Failed to close ${window}: ${Cleanup.of.webdriverExceptionMessage(windowHasBeenClosedMeanwhile)}" }
        } catch (e: Exception) {
            log.warn(e) { "  Failed to close $window" }
        }
    }

    companion object {
        private val log = logging(WindowsCloser::class.simpleName)
    }
}

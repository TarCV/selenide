package org.openqa.selenium

import org.openqa.selenium.logging.LogEntries
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual interface WebDriver: org.openqa.selenium.SearchContext {
    actual val currentUrl: String
    actual val pageSource: String
    actual val title: String
    actual val windowHandle: String
    actual val windowHandles: Set<String>

    actual fun manage(): Manager
    actual fun navigate(): Navigator
    actual fun switchTo(): TargetLocator
    actual fun quit()
    actual fun close()

    actual interface Manager {
        actual suspend fun deleteAllCookies()
        @ExperimentalTime
        actual fun timeouts(): Timeouts
        actual fun logs(): Logs
    }

    @ExperimentalTime
    actual interface Timeouts {
        actual fun pageLoadTimeout(duration: Duration)

    }

    actual interface Navigator {
        actual suspend fun to(url: String)
        actual suspend fun back()
        actual suspend fun forward()
        actual suspend fun refresh()
    }
    actual interface TargetLocator {
        actual suspend fun frame(index: Int): org.openqa.selenium.WebDriver
        actual suspend fun frame(nameOrId: String): org.openqa.selenium.WebDriver
        actual suspend fun frame(frameElement: org.openqa.selenium.WebElement): org.openqa.selenium.WebDriver
        actual suspend fun parentFrame(): org.openqa.selenium.WebDriver
        actual suspend fun defaultContent(): org.openqa.selenium.WebDriver
        actual suspend fun activeElement(): org.openqa.selenium.WebElement
        actual suspend fun alert(): org.openqa.selenium.Alert
        actual suspend fun window(nameOrHandleOrTitle: String): org.openqa.selenium.WebDriver
    }
    actual interface Logs {
        actual operator fun get(logType: String): org.openqa.selenium.logging.LogEntries
    }
}

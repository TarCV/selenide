package org.openqa.selenium

import org.openqa.selenium.logging.LogEntries
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface WebDriver: SearchContext {
    val currentUrl: String
    val pageSource: String
    val title: String
    val windowHandle: String
    val windowHandles: Set<String>

    fun manage(): Manager
    fun navigate(): Navigator
    fun switchTo(): TargetLocator
    fun quit()
    fun close()

    interface Manager {
        suspend fun deleteAllCookies()
        @ExperimentalTime
        fun timeouts(): Timeouts
        fun logs(): Logs
    }

    @ExperimentalTime
    interface Timeouts {
        fun pageLoadTimeout(duration: Duration)

    }

    interface Navigator {
        suspend fun to(url: String)
        suspend fun back()
        suspend fun forward()
        suspend fun refresh()
    }
    interface TargetLocator {
        suspend fun frame(index: Int): WebDriver
        suspend fun frame(nameOrId: String): WebDriver
        suspend fun frame(frameElement: WebElement): WebDriver
        suspend fun parentFrame(): WebDriver
        suspend fun defaultContent(): WebDriver
        suspend fun activeElement(): WebElement
        suspend fun alert(): Alert
        suspend fun window(nameOrHandleOrTitle: String): WebDriver
    }
    interface Logs {
        operator fun get(logType: String): LogEntries = TODO()
    }
}

package org.openqa.selenium

import org.openqa.selenium.logging.LogEntries
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

expect interface WebDriver: SearchContext {
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
        suspend fun frame(index: Int): org.openqa.selenium.WebDriver
        suspend fun frame(nameOrId: String): org.openqa.selenium.WebDriver
        suspend fun frame(frameElement: org.openqa.selenium.WebElement): org.openqa.selenium.WebDriver
        suspend fun parentFrame(): org.openqa.selenium.WebDriver
        suspend fun defaultContent(): org.openqa.selenium.WebDriver
        suspend fun activeElement(): org.openqa.selenium.WebElement
        suspend fun alert(): org.openqa.selenium.Alert
        suspend fun window(nameOrHandleOrTitle: String): org.openqa.selenium.WebDriver
    }
    interface Logs {
        operator fun get(logType: String): org.openqa.selenium.logging.LogEntries
    }
}

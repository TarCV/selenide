package com.codeborne.selenide

import org.openqa.selenium.logging.LogEntries
import org.openqa.selenium.logging.LogEntry
import java.util.Collections
import java.util.logging.Level
import java.util.stream.Collectors
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class WebDriverLogs internal constructor(private val driver: Driver) {
    @CheckReturnValue
    fun logs(logType: String): List<String> {
        return logs(logType, Level.ALL)
    }

    @CheckReturnValue
    fun logs(logType: String, logLevel: Level): List<String> {
        return listToString(getLogEntries(logType, logLevel))
    }

    @CheckReturnValue
    private fun getLogEntries(logType: String, logLevel: Level): List<LogEntry> {
        return try {
            filter(driver.webDriver.manage().logs()[logType], logLevel)
        } catch (ignore: UnsupportedOperationException) {
            emptyList()
        }
    }

    private fun filter(entries: LogEntries, level: Level): List<LogEntry> {
        return Collections.unmodifiableList(entries.all.stream()
            .filter { entry: LogEntry -> entry.level.intValue() >= level.intValue() }
            .collect(Collectors.toList()))
    }

    @CheckReturnValue
    private fun <T> listToString(objects: List<T>): List<String> {
        return objects.stream().map { obj: T -> obj.toString() }.collect(Collectors.toList())
    }
}

package com.codeborne.selenide

import org.openqa.selenium.logging.LogEntries
import org.openqa.selenium.logging.LogEntry
import support.logging.Level

class WebDriverLogs internal constructor(private val driver: Driver) {
    fun logs(logType: String): List<String> {
        return logs(logType, Level.ALL)
    }
    fun logs(logType: String, logLevel: Level): List<String> {
        return listToString(getLogEntries(logType, logLevel))
    }
    private fun getLogEntries(logType: String, logLevel: Level): List<org.openqa.selenium.logging.LogEntry> {
        return try {
            filter(driver.webDriver.manage().logs().get(logType), logLevel)
        } catch (ignore: UnsupportedOperationException) {
            emptyList()
        }
    }

    private fun filter(entries: org.openqa.selenium.logging.LogEntries, level: Level): List<org.openqa.selenium.logging.LogEntry> {
        return entries.all
            .filter { entry -> entry.level.intValue() >= level.intValue() }
    }
    private fun <T> listToString(objects: List<T>): List<String> {
        return objects.map { obj: T -> obj.toString() }
    }
}

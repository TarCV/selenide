package com.codeborne.selenide.logevents

import org.slf4j.LoggerFactory
import org.slf4j.helpers.NOPLogger
import org.slf4j.helpers.NOPLoggerFactory
import java.util.Collections
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

/**
 * A simple text report of Selenide actions performed during test run.
 *
 * Class is thread-safe: the same instance of SimpleReport can be reused by different threads simultaneously.
 */
@ParametersAreNonnullByDefault
class SimpleReport {
    fun start() {
        checkThatSlf4jIsConfigured()
        SelenideLogger.addListener("simpleReport", EventsCollector())
    }

    fun finish(title: String?) {
        val logEventListener = SelenideLogger.removeListener<EventsCollector>("simpleReport")
        if (logEventListener == null) {
            log.warn("Can not publish report because Selenide logger has not started.")
            return
        }
        val maxLineLength = logEventListener.events()
            .map(LogEvent::element)
            .map { obj -> obj.length }
          .maxOrNull() ?: 0
        val count = if (maxLineLength >= 20) maxLineLength + 1 else 20
        val sb = StringBuilder()
        sb.append("Report for ").append(title).append('\n')
        val delimiter = """
               +${java.lang.String.join("+", line(count), line(70), line(10), line(10))}+

               """.trimIndent()
        sb.append(delimiter)
        sb.append(String.format("|%-" + count + "s|%-70s|%-10s|%-10s|%n", "Element", "Subject", "Status", "ms."))
        sb.append(delimiter)
        for (e in logEventListener.events()) {
            sb.append(
                String.format(
                    "|%-" + count + "s|%-70s|%-10s|%-10s|%n", e.element, e.subject,
                    e.status, e.duration
                )
            )
        }
        sb.append(delimiter)
        log.info(sb.toString())
    }

    fun clean() {
        SelenideLogger.removeListener<LogEventListener>("simpleReport")
    }

    @CheckReturnValue
    private fun line(count: Int): String {
        return java.lang.String.join("", Collections.nCopies(count, "-"))
    }

    companion object {
        private val log = LoggerFactory.getLogger(SimpleReport::class.java)
        private fun checkThatSlf4jIsConfigured() {
            val loggerFactory = LoggerFactory.getILoggerFactory()
            check(!(loggerFactory is NOPLoggerFactory || loggerFactory.getLogger("com.codeborne.selenide") is NOPLogger)) {
                """SLF4J is not configured. You will not see any Selenide logs.
  Please add slf4j-simple.jar, slf4j-log4j12.jar or logback-classic.jar to your classpath.
  See https://github.com/selenide/selenide/wiki/slf4j"""
            }
        }
    }
}

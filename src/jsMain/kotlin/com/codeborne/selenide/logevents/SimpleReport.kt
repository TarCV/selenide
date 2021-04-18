package com.codeborne.selenide.logevents

import org.slf4j.LoggerFactory

/**
 * A simple text report of Selenide actions performed during test run.
 *
 * Class is thread-safe: the same instance of SimpleReport can be reused by different threads simultaneously.
 */
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
               +${"-".repeat(count)}+${"-".repeat(70)}+${"-".repeat(10)}+${"-".repeat(10)}"}+

               """.trimIndent()
        sb.append(delimiter)

        sb.append("|${"Element".padEnd(count)}")
        sb.append("|${"Subject".padEnd(70)}")
        sb.append("|${"Status".padEnd(10)}")
        sb.appendLine("|${"ms.".padEnd(10)}|")

        sb.append(delimiter)
        for (e in logEventListener.events()) {
            val subject = e.subject ?: ""
            val status = e.status.toString()
            val duration = e.duration.toString()

            sb.append("|${e.element.padEnd(70)}")
            sb.append("|${subject.padEnd(70)}")
            sb.append("|${status.padEnd(10)}")
            sb.appendLine("|${duration.padEnd(10)}|")
        }
        sb.append(delimiter)
        log.info(sb.toString())
    }

    fun clean() {
        SelenideLogger.removeListener<LogEventListener>("simpleReport")
    }

    companion object {
        private val log = LoggerFactory.getLogger(SimpleReport::class)
        private fun checkThatSlf4jIsConfigured() {
/* TODO:
            val loggerFactory = LoggerFactory.getILoggerFactory()
            check(!(loggerFactory is NOPLoggerFactory || loggerFactory.getLogger("com.codeborne.selenide") is NOPLogger)) {
                """SLF4J is not configured. You will not see any Selenide logs.
  Please add slf4j-simple.jar, slf4j-log4j12.jar or logback-classic.jar to your classpath.
  See https://github.com/selenide/selenide/wiki/slf4j"""
            }
*/
        }
    }
}

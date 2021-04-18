package com.codeborne.selenide.logevents

import com.codeborne.selenide.AssertionMode
import com.codeborne.selenide.Config
import com.codeborne.selenide.ex.SoftAssertionError
import com.codeborne.selenide.logevents.LogEvent.EventStatus
import com.codeborne.selenide.logevents.SelenideLogger.hasListener

class ErrorsCollector : LogEventListener {
    private val errors: MutableList<Throwable?> = ArrayList()
    override fun afterEvent(currentLog: LogEvent) {
        if (currentLog.status === EventStatus.FAIL) {
            errors.add(currentLog.error)
        }
    }

    override fun beforeEvent(currentLog: LogEvent) {
        // ignore
    }

    fun clear() {
        errors.clear()
    }

    fun getErrors(): List<Throwable?> {
        return (errors)
    }

    /**
     * 1. Clears all collected errors, and
     * 2. throws SoftAssertionError if there were some errors
     *
     * @param testName any string, usually name of current test
     */
    fun failIfErrors(testName: String?) {
        val errors: List<Throwable?> = ArrayList(errors)
        this.errors.clear()
        if (errors.size == 1) {
            throw SoftAssertionError(errors[0].toString())
        }
        if (errors.isNotEmpty()) {
            val sb = StringBuilder()
            sb.append("Test ").append(testName).append(" failed.").appendLine()
            sb.append(errors.size).append(" checks failed").appendLine()
            var i = 0
            for (error in errors) {
                sb.appendLine().append("FAIL #").append(++i).append(": ")
                sb.append(error).appendLine()
            }
            throw SoftAssertionError(sb.toString())
        }
    }

    companion object {
        const val LISTENER_SOFT_ASSERT = "softAssert"
        fun validateAssertionMode(config: Config) {
            if (config.assertionMode() === AssertionMode.SOFT) {
                check(hasListener(LISTENER_SOFT_ASSERT)) {
                    "You must configure you classes using JUnit4/JUnit5/TestNG " +
                            "mechanism as documented in https://github.com/selenide/selenide/wiki/SoftAssertions"
                }
            }
        }
    }
}

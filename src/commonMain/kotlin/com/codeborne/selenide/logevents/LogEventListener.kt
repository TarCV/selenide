package com.codeborne.selenide.logevents


/**
 * An implementations of this interface can be registered by **SelenideLogger#addListener** <br></br>
 * It will notified on each events emitted by Selenide
 *
 * @see SelenideLogger
 */
interface LogEventListener {
    fun afterEvent(currentLog: LogEvent)
    fun beforeEvent(currentLog: LogEvent)
}

package com.codeborne.selenide.logevents

import javax.annotation.ParametersAreNonnullByDefault

/**
 * An implementations of this interface can be registered by **SelenideLogger#addListener** <br></br>
 * It will notified on each events emitted by Selenide
 *
 * @see SelenideLogger
 */
@ParametersAreNonnullByDefault
interface LogEventListener {
    fun afterEvent(currentLog: LogEvent)
    fun beforeEvent(currentLog: LogEvent)
}

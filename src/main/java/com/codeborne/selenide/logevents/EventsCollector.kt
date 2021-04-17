package com.codeborne.selenide.logevents

import java.util.Collections
import javax.annotation.CheckReturnValue

class EventsCollector : LogEventListener {
    private val logEvents: MutableList<LogEvent> = ArrayList()
    override fun afterEvent(currentLog: LogEvent) {
        logEvents.add(currentLog)
    }

    override fun beforeEvent(currentLog: LogEvent) {
        //ignore
    }

    @CheckReturnValue
    fun events(): List<LogEvent> {
        return Collections.unmodifiableList(logEvents)
    }
}

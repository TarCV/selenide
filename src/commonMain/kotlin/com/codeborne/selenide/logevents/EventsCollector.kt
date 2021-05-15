package com.codeborne.selenide.logevents


class EventsCollector : LogEventListener {
    private val logEvents: MutableList<LogEvent> = ArrayList()
    override fun afterEvent(logEvent: LogEvent) {
        logEvents.add(logEvent)
    }

    override fun beforeEvent(logEvent: LogEvent) {
        //ignore
    }
    fun events(): List<LogEvent> {
        return (logEvents)
    }
}

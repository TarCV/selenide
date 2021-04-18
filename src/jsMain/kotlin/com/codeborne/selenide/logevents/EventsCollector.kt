package com.codeborne.selenide.logevents


class EventsCollector : LogEventListener {
    private val logEvents: MutableList<LogEvent> = ArrayList()
    override fun afterEvent(currentLog: LogEvent) {
        logEvents.add(currentLog)
    }

    override fun beforeEvent(currentLog: LogEvent) {
        //ignore
    }
    fun events(): List<LogEvent> {
        return (logEvents)
    }
}

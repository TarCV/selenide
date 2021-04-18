package com.codeborne.selenide.logevents

import com.codeborne.selenide.logevents.LogEvent.EventStatus
import support.System

open class SelenideLog(override val element: String, override val subject: String) : LogEvent {
    private val startNs: Long = System.nanoTime()
    private var endNs: Long = 0

    override var status = EventStatus.IN_PROGRESS
        internal set(status) {
            field = status
            endNs = System.nanoTime()
        }
    override var error: Throwable? = null
    override val duration: Long
        get() = (endNs - startNs) / 1000000

    override fun toString(): String {
        return "$(\"${element}\") $subject"
    }

}

package com.codeborne.selenide.logevents

/**
 * Events, created on Selenide actions
 * like "navigate to url", "click on element", "check a condition" <br></br><br></br>
 *
 * An event contains a string representation of the element, the subject and its status.
 */
interface LogEvent {
    enum class EventStatus {
        IN_PROGRESS, PASS, FAIL
    }

    val element: String
    val subject: String?
    val status: EventStatus?
    val duration: Long
    val error: Throwable?
}

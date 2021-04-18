package com.codeborne.selenide.impl

import kotlin.time.Duration

class DurationFormat {
    @kotlin.time.ExperimentalTime
    fun format(duration: Duration): String {
        return format(duration.toLongMilliseconds())
    }
    fun format(milliseconds: Long): String {
        if (milliseconds < 1000) {
            return "$milliseconds ms."
        }
        return if (milliseconds % 1000 == 0L) {
            "${milliseconds / 1000} s."
        } else {
            val seconds = (milliseconds / 1000.0).toString()
            val exact = seconds.substringBefore('.')
            val fractional = seconds.substringAfter('.').substring(0, 3)
            "$exact.$fractional s."
        }
    }
}

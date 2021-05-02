package com.codeborne.selenide

import kotlinx.coroutines.delay
import support.System
import kotlin.time.milliseconds

class Stopwatch(timeoutMs: Long) {
    @kotlin.time.ExperimentalTime
    private val endTimeNano: Long = System.nanoTime() + timeoutMs.milliseconds.toLongNanoseconds()

    @kotlin.time.ExperimentalTime
    val isTimeoutReached: Boolean
        get() = System.nanoTime() > endTimeNano

    @kotlin.time.ExperimentalTime
    suspend fun sleep(milliseconds: Long) {
        if (isTimeoutReached) return
        delay(milliseconds)
    }

}

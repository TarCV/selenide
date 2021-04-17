package com.codeborne.selenide

import com.google.errorprone.annotations.CheckReturnValue
import java.util.concurrent.TimeUnit
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Stopwatch(timeoutMs: Long) {
    private val endTimeNano: Long = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs)

  @get:CheckReturnValue
    val isTimeoutReached: Boolean
        get() = System.nanoTime() > endTimeNano

    fun sleep(milliseconds: Long) {
        if (isTimeoutReached) return
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException(e)
        }
    }

}

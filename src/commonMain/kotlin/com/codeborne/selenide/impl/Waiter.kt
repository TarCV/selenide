package com.codeborne.selenide.impl

import kotlinx.coroutines.delay
import support.System

open class Waiter {
    open suspend fun <T> wait(subject: T, condition: (T) -> Boolean, timeout: Long, pollingInterval: Long) {
        sleep(pollingInterval)
        val start = System.currentTimeMillis()
        while (!isTimeoutExceeded(timeout, start) && !condition(subject)) {
            sleep(pollingInterval)
        }
    }

    private fun isTimeoutExceeded(timeout: Long, start: Long): Boolean {
        return System.currentTimeMillis() - start > timeout
    }

    private suspend fun sleep(milliseconds: Long) {
        delay(milliseconds)
    }
}

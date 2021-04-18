package com.codeborne.selenide.impl

import java.util.function.Predicate
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
internal open class Waiter {
    @CheckReturnValue
    open fun <T> wait(subject: T, condition: Predicate<T>, timeout: Long, pollingInterval: Long) {
        sleep(pollingInterval)
        val start = System.currentTimeMillis()
        while (!isTimeoutExceeded(timeout, start) && !condition.test(subject)) {
            sleep(pollingInterval)
        }
    }

    private fun isTimeoutExceeded(timeout: Long, start: Long): Boolean {
        return System.currentTimeMillis() - start > timeout
    }

    private fun sleep(milliseconds: Long) {
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException(e)
        }
    }
}

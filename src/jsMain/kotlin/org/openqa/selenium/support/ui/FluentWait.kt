package org.openqa.selenium.support.ui

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

open class FluentWait<T>(input: T) {
    @ExperimentalTime
    fun withTimeout(duration: Duration): FluentWait<T> = TODO()
    @ExperimentalTime
    fun pollingEvery(duration: Duration): FluentWait<T> = TODO()

    suspend fun until(predicate: () -> Boolean): T = TODO()
    suspend fun <E: Any> until(predicate: ExpectedCondition<E>): E = TODO()
}

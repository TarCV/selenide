package org.openqa.selenium.support.ui

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

expect open class FluentWait<T> actual constructor(input: T) {
    fun withTimeout(duration: Duration): FluentWait<T>
    fun pollingEvery(duration: Duration): FluentWait<T>

    suspend fun until(predicate: () -> Boolean): T
    suspend fun <E: Any> until(predicate: ExpectedCondition<E>): E
}

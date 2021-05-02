package org.openqa.selenium.support.ui

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual open class FluentWait<T> actual constructor(input: T) {
    @ExperimentalTime
    actual fun withTimeout(duration: Duration): FluentWait<T> = TODO()
    @ExperimentalTime
    actual fun pollingEvery(duration: Duration): FluentWait<T> = TODO()

    suspend actual fun until(predicate: () -> Boolean): T = TODO()
    suspend actual fun <E: Any> until(predicate: ExpectedCondition<E>): E = TODO()
}

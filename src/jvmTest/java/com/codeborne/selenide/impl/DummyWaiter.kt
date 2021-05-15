package com.codeborne.selenide.impl

import java.util.function.Predicate
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class DummyWaiter : Waiter() {
    @CheckReturnValue
    override suspend fun <T> wait(subject: T, condition: (T) -> Boolean, timeout: Long, pollingInterval: Long) {
        condition(subject)
    }
}

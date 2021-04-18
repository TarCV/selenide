package com.codeborne.selenide.impl

import java.time.Duration
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class DurationFormat {
    @CheckReturnValue
    fun format(duration: Duration): String {
        return format(duration.toMillis())
    }

    @CheckReturnValue
    fun format(milliseconds: Long): String {
        if (milliseconds < 1000) {
            return String.format("%d ms.", milliseconds)
        }
        return if (milliseconds % 1000 == 0L) {
            String.format("%d s.", milliseconds / 1000)
        } else String.format("%.3f s.", milliseconds / 1000.0)
    }
}

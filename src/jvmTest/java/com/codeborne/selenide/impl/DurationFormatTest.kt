package com.codeborne.selenide.impl

import integration.UseLocaleExtension
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.minutes
import kotlin.time.seconds

@ExperimentalTime
internal class DurationFormatTest {
    private val df = DurationFormat()
    @Test
    fun zero() {
        Assertions.assertThat(df.format(0)).isEqualTo("0 ms.")
    }

    @Test
    fun lessThanSecond() {
        Assertions.assertThat(df.format(1)).isEqualTo("1 ms.")
        Assertions.assertThat(df.format(999)).isEqualTo("999 ms.")
    }

    @Test
    fun integerSeconds() {
        Assertions.assertThat(df.format(1000)).isEqualTo("1 s.")
        Assertions.assertThat(df.format(2000)).isEqualTo("2 s.")
    }

    @Test
    fun greaterThanSecond() {
        Assertions.assertThat(df.format(1500)).isEqualTo("1.500 s.")
        Assertions.assertThat(df.format(1567)).isEqualTo("1.567 s.")
        Assertions.assertThat(df.format(2001)).isEqualTo("2.001 s.")
    }

    @Test
    fun greaterThanMinute() {
        Assertions.assertThat(df.format(1.minutes)).isEqualTo("60 s.")
        Assertions.assertThat(df.format(2.minutes + 2.seconds)).isEqualTo("122 s.")
        Assertions.assertThat(df.format(3.minutes + 300.milliseconds)).isEqualTo("180.300 s.")
    }

    companion object {
        @RegisterExtension
        var useLocale = UseLocaleExtension("en")
    }
}

package com.codeborne.selenide

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SelenideWait(input: WebDriver, timeout: Long, pollingInterval: Long) : FluentWait<WebDriver>(input) {
    init {
        withTimeout(Duration.of(timeout, ChronoUnit.MILLIS))
        pollingEvery(Duration.of(pollingInterval, ChronoUnit.MILLIS))
    }
}

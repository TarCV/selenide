package com.codeborne.selenide

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait
import kotlin.time.Duration

@kotlin.time.ExperimentalTime
class SelenideWait(input: WebDriver, timeout: Long, pollingInterval: Long) : FluentWait<WebDriver>(input) {
    init {
        withTimeout(Duration.milliseconds(timeout))
        pollingEvery(Duration.milliseconds(pollingInterval))
    }
}

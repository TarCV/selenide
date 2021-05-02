package com.codeborne.selenide

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait
import java.util.concurrent.TimeUnit;
import kotlin.time.Duration
import kotlin.time.milliseconds

@kotlin.time.ExperimentalTime
class SelenideWait(input: org.openqa.selenium.WebDriver, timeout: Long, pollingInterval: Long) : FluentWait<org.openqa.selenium.WebDriver>(input) {
    init {
        withTimeout(timeout, TimeUnit.MILLISECONDS)
        pollingEvery(pollingInterval, TimeUnit.MILLISECONDS)
    }
}

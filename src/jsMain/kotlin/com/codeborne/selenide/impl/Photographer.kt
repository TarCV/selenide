package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.OutputType

interface Photographer {
    fun <T: Any> takeScreenshot(driver: Driver, outputType: OutputType<T>): T?
}
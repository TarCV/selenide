package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot

class WebdriverPhotographer : Photographer {
    override fun <T: Any> takeScreenshot(driver: Driver, outputType: OutputType<T>): T? {
        if (driver.webDriver is TakesScreenshot) {
            val screenshot = (driver.webDriver as TakesScreenshot).getScreenshotAs(outputType)
            return (screenshot)
        }
        return null
    }
}

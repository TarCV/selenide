package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class Enabled : Condition("enabled") {
    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        return element.isEnabled
    }

    override suspend fun actualValue(driver: Driver, element: WebElement): String {
        return if (element.isEnabled) "enabled" else "disabled"
    }
}

package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class Selected : Condition("selected") {
    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        return element.isSelected
    }

    override suspend fun actualValue(driver: Driver, element: WebElement): String? {
        return element.isSelected.toString()
    }
}

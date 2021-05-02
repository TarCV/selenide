package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class Checked : Condition("checked") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return element.isSelected
    }

    override suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String {
        return element.isSelected.toString()
    }
}

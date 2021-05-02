package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class Visible : Condition("visible") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return element.isDisplayed
    }
    override suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String {
        return "visible:${element.isDisplayed}"
    }
    override fun negate(): Condition {
        return Not(this, true)
    }
}

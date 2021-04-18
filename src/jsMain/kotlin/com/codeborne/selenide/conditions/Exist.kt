package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement

class Exist : Condition("exist") {
    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        return try {
            element.isDisplayed
            true
        } catch (e: StaleElementReferenceException) {
            false
        }
    }

    override fun negate(): Condition {
        return Not(this, true)
    }
}

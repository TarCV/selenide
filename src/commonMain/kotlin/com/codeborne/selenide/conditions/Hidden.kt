package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement

class Hidden : Condition("hidden", true) {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return try {
            !element.isDisplayed
        } catch (elementHasDisappeared: org.openqa.selenium.StaleElementReferenceException) {
            true
        }
    }

    override fun negate(): Condition {
        return Not(this, false)
    }
}

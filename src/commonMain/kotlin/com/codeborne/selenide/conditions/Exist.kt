package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement

class Exist : Condition("exist") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return try {
            element.isDisplayed()
            true
        } catch (e: org.openqa.selenium.StaleElementReferenceException) {
            false
        }
    }

    override fun negate(): Condition {
        return Not(this, true)
    }
}

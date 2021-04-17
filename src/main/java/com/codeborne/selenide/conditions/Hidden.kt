package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Hidden : Condition("hidden", true) {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return try {
            !element.isDisplayed
        } catch (elementHasDisappeared: StaleElementReferenceException) {
            true
        }
    }

    override fun negate(): Condition {
        return Not(this, false)
    }
}

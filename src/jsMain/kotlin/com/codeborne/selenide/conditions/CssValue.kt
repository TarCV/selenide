package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class CssValue(private val propertyName: String, private val expectedValue: String?) : Condition("css value") {
    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        val actualCssValue = element.getCssValue(propertyName)
        return (expectedValue ?: "")
            .equals((actualCssValue ?: ""), ignoreCase = true)
    }

    override suspend fun actualValue(driver: Driver, element: WebElement): String? {
        return element.getCssValue(propertyName)
    }

    override fun toString(): String {
        return "$name $propertyName=$expectedValue"
    }
}

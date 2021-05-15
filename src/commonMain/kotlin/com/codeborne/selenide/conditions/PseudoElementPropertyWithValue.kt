package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class PseudoElementPropertyWithValue(
    private val pseudoElementName: String,
    private val propertyName: String,
    private val expectedPropertyValue: String
) : Condition("pseudo-element") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return (expectedPropertyValue ?: "")
            .equals(getPseudoElementPropertyValue(driver, element), ignoreCase = true)
    }

    override suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String {
        return "$pseudoElementName {$propertyName: ${getPseudoElementPropertyValue(driver, element)};}"
    }

    override fun toString(): String {
        return "$name $pseudoElementName {$propertyName: $expectedPropertyValue;}"
    }

    private suspend fun getPseudoElementPropertyValue(driver: Driver, element: org.openqa.selenium.WebElement): String {
        val propertyValue = driver.executeJavaScript<String>(JS_CODE, element, pseudoElementName, propertyName)
        return propertyValue ?: ""
    }

    companion object {
        const val JS_CODE = "return window.getComputedStyle(arguments[0], arguments[1])" +
                ".getPropertyValue(arguments[2]);"
    }
}

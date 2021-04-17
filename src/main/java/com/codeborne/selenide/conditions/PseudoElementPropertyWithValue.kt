package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class PseudoElementPropertyWithValue(
    private val pseudoElementName: String,
    private val propertyName: String,
    private val expectedPropertyValue: String
) : Condition("pseudo-element") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return StringUtils.defaultString(expectedPropertyValue)
            .equals(getPseudoElementPropertyValue(driver, element), ignoreCase = true)
    }

    override fun actualValue(driver: Driver, element: WebElement): String {
        return String.format(
            "%s {%s: %s;}", pseudoElementName, propertyName,
            getPseudoElementPropertyValue(driver, element)
        )
    }

    override fun toString(): String {
        return String.format("%s %s {%s: %s;}", name, pseudoElementName, propertyName, expectedPropertyValue)
    }

    private fun getPseudoElementPropertyValue(driver: Driver, element: WebElement): String {
        val propertyValue = driver.executeJavaScript<String>(JS_CODE, element, pseudoElementName, propertyName)
        return propertyValue ?: StringUtils.EMPTY
    }

    companion object {
        const val JS_CODE = "return window.getComputedStyle(arguments[0], arguments[1])" +
                ".getPropertyValue(arguments[2]);"
    }
}

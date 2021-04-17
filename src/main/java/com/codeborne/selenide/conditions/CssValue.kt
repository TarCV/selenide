package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class CssValue(private val propertyName: String, private val expectedValue: String?) : Condition("css value") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        val actualCssValue = element.getCssValue(propertyName)
        return StringUtils.defaultString(expectedValue)
            .equals(StringUtils.defaultString(actualCssValue), ignoreCase = true)
    }

    override fun actualValue(driver: Driver, element: WebElement): String? {
        return element.getCssValue(propertyName)
    }

    override fun toString(): String {
        return "$name $propertyName=$expectedValue"
    }
}

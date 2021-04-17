package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class AttributeWithValue(private val attributeName: String, protected val expectedAttributeValue: String) :
    Condition("attribute") {
    @CheckReturnValue
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return expectedAttributeValue == getAttributeValue(element)
    }

    @CheckReturnValue
    override fun actualValue(driver: Driver, element: WebElement): String {
        return String.format("%s=\"%s\"", attributeName, getAttributeValue(element))
    }

    @CheckReturnValue
    override fun toString(): String {
        return String.format("%s %s=\"%s\"", name, attributeName, expectedAttributeValue)
    }

    protected fun getAttributeValue(element: WebElement): String {
        val attr = element.getAttribute(attributeName)
        return attr ?: ""
    }
}

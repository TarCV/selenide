package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

open class AttributeWithValue(private val attributeName: String, protected val expectedAttributeValue: String) :
    Condition("attribute") {
    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        return expectedAttributeValue == getAttributeValue(element)
    }
    override suspend fun actualValue(driver: Driver, element: WebElement): String {
        return "$attributeName=\"${getAttributeValue(element)}\""
    }
    override fun toString(): String {
        return "$name $attributeName=\"$expectedAttributeValue\""
    }

    protected suspend fun getAttributeValue(element: WebElement): String {
        val attr = element.getAttribute(attributeName)
        return attr ?: ""
    }
}

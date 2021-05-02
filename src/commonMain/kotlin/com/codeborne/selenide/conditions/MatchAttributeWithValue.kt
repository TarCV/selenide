package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class MatchAttributeWithValue(private val attributeName: String, attributeRegex: String) :
    Condition("match attribute") {
    private val attributeRegex: kotlin.text.Regex = kotlin.text.Regex(attributeRegex)
  override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return attributeRegex.matches(getAttributeValue(element))
    }

    override suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String {
        return "$attributeName=\"${getAttributeValue(element)}\""
    }

    override fun toString(): String {
        return "$name $attributeName=\"$attributeRegex\""
    }

    private suspend fun getAttributeValue(element: org.openqa.selenium.WebElement): String {
        val attr = element.getAttribute(attributeName)
        return attr ?: ""
    }

}

package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import java.util.regex.Pattern
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class MatchAttributeWithValue(private val attributeName: String, attributeRegex: String) :
    Condition("match attribute") {
    private val attributeRegex: Pattern = Pattern.compile(attributeRegex)
  override fun apply(driver: Driver, element: WebElement): Boolean {
        return attributeRegex.matcher(getAttributeValue(element)).matches()
    }

    override fun actualValue(driver: Driver, element: WebElement): String {
        return String.format("%s=\"%s\"", attributeName, getAttributeValue(element))
    }

    override fun toString(): String {
        return String.format("%s %s=\"%s\"", name, attributeName, attributeRegex)
    }

    private fun getAttributeValue(element: WebElement): String {
        val attr = element.getAttribute(attributeName)
        return attr ?: ""
    }

}

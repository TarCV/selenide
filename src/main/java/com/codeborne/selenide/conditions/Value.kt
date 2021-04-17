package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Value(private val expectedValue: String) : Condition("value") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return Html.text.contains(getValueAttribute(element), expectedValue)
    }

    override fun toString(): String {
        return "$name '$expectedValue'"
    }

    private fun getValueAttribute(element: WebElement): String {
        val attr = element.getAttribute("value")
        return attr ?: ""
    }
}

package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement

class Value(private val expectedValue: String) : Condition("value") {
    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        return Html.text.contains(getValueAttribute(element), expectedValue)
    }

    override fun toString(): String {
        return "$name '$expectedValue'"
    }

    private suspend fun getValueAttribute(element: WebElement): String {
        val attr = element.getAttribute("value")
        return attr ?: ""
    }
}

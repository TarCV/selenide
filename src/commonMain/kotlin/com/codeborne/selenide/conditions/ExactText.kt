package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import com.codeborne.selenide.impl.Html

class ExactText(private val expectedText: String) : Condition("exact text") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return Html.text.equals(element.text, expectedText)
    }

    override fun toString(): String {
        return "$name '$expectedText'"
    }
}

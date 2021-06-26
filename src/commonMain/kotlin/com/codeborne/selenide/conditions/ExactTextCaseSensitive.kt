package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement

class ExactTextCaseSensitive(private val expectedText: String) : Condition("exact text case sensitive") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return Html.text.equalsCaseSensitive(element.text, expectedText)
    }

    override fun toString(): String {
        return "${name} '${expectedText}'"
    }
}

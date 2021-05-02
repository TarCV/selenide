package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class SelectedText(private val expectedText: String) : Condition("selectedText") {
    private var actualResult = ""
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        actualResult = driver.executeJavaScript(
            "return arguments[0].value.substring(arguments[0].selectionStart, arguments[0].selectionEnd);", element
        )
        return actualResult == expectedText
    }

    override suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String {
        return "'$actualResult'"
    }

    override fun toString(): String {
        return "${name} '{expectedText}'"
    }
}

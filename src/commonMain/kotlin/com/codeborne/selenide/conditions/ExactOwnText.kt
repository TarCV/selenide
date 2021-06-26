package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.commands.GetOwnText.Companion.getOwnText
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement

class ExactOwnText(private val expectedText: String) : Condition("exact own text") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return Html.text.equals(getOwnText(driver, element), expectedText)
    }

    override fun toString(): String {
        return "${name} '${expectedText}'"
    }

    override suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String {
        return getOwnText(driver, element)
    }
}

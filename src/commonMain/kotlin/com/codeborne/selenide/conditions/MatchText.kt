package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement

class MatchText(private val regex: String) : Condition("match text") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return Html.text.matches(element.text, regex)
    }

    override fun toString(): String {
        return "${name} '${regex}'"
    }
}

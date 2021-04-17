package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.commands.GetOwnText.Companion.getOwnText
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ExactOwnText(private val expectedText: String) : Condition("exact own text") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return Html.text.equals(getOwnText(driver, element), expectedText)
    }

    override fun toString(): String {
        return String.format("%s '%s'", name, expectedText)
    }

    override fun actualValue(driver: Driver, element: WebElement): String {
        return getOwnText(driver, element)
    }
}

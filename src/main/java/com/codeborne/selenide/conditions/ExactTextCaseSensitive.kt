package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ExactTextCaseSensitive(private val expectedText: String) : Condition("exact text case sensitive") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return Html.text.equalsCaseSensitive(element.text, expectedText)
    }

    override fun toString(): String {
        return String.format("%s '%s'", name, expectedText)
    }
}

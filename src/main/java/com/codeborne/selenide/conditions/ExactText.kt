package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import javax.annotation.ParametersAreNonnullByDefault
import org.openqa.selenium.WebElement
import com.codeborne.selenide.impl.Html

@ParametersAreNonnullByDefault
class ExactText(private val expectedText: String) : Condition("exact text") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return Html.text.equals(element.text, expectedText)
    }

    override fun toString(): String {
        return String.format("%s '%s'", name, expectedText)
    }
}

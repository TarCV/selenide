package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class MatchText(private val regex: String) : Condition("match text") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return Html.text.matches(element.text, regex)
    }

    override fun toString(): String {
        return String.format("%s '%s'", name, regex)
    }
}

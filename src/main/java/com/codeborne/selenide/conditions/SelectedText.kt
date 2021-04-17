package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SelectedText(private val expectedText: String) : Condition("selectedText") {
    private var actualResult = ""
    override fun apply(driver: Driver, element: WebElement): Boolean {
        actualResult = driver.executeJavaScript(
            "return arguments[0].value.substring(arguments[0].selectionStart, arguments[0].selectionEnd);", element
        )
        return actualResult == expectedText
    }

    override fun actualValue(driver: Driver, element: WebElement): String {
        return "'$actualResult'"
    }

    override fun toString(): String {
        return String.format("%s '%s'", name, expectedText)
    }
}

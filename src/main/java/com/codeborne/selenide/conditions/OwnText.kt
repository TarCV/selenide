package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.commands.GetOwnText.Companion.getOwnText
import com.codeborne.selenide.impl.Html
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class OwnText(private val expectedText: String) : Condition("own text") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return Html.text.contains(getOwnText(driver, element), expectedText)
    }

    override fun toString(): String {
        return String.format("%s '%s'", name, expectedText)
    }

    override fun actualValue(driver: Driver, element: WebElement): String {
        return getOwnText(driver, element)
    }

    init {
        require(!StringUtils.isEmpty(expectedText)) {
            "Argument must not be null or empty string. " +
                    "Use $.shouldHave(exactOwnText(\"\")."
        }
    }
}

package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select

class Text(protected val text: String) : Condition("text") {
    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        val elementText =
            if ("select".equals(element.tagName, ignoreCase = true)) getSelectedOptionsTexts(element) else element.text
        return Html.text.contains(elementText, text.toLowerCase())
    }

    private fun getSelectedOptionsTexts(element: WebElement): String {
        val selectedOptions = Select(element).allSelectedOptions
        val sb = StringBuilder()
        for (selectedOption in selectedOptions) {
            sb.append(selectedOption.text)
        }
        return sb.toString()
    }

    override fun toString(): String {
        return "$name '{text}'"
    }

    init {
        require(text.isNotBlank()) {
            "Argument must not be null or empty string. " +
                    "Use $.shouldBe(empty) or $.shouldHave(exactText(\"\")."
        }
    }
}
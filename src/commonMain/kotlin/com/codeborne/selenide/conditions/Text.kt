package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select

class Text(protected val text: String) : Condition("text") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        val elementText =
            if ("select".equals(element.getTagName(), ignoreCase = true)) getSelectedOptionsTexts(element) else element.getText()
        return Html.text.contains(elementText, text.toLowerCase())
    }

    private suspend fun getSelectedOptionsTexts(element: org.openqa.selenium.WebElement): String {
        val selectedOptions = Select(element).allSelectedOptions
        val sb = StringBuilder()
        for (selectedOption in selectedOptions) {
            sb.append(selectedOption.getText())
        }
        return sb.toString()
    }

    override fun toString(): String {
        return "$name '${text}'"
    }

    init {
        require(text.isNotEmpty()) {
            "Argument must not be null or empty string. " +
                    "Use $.shouldBe(empty) or $.shouldHave(exactText(\"\")."
        }
    }
}

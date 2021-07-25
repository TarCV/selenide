package com.codeborne.selenide.collections

import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement

class ExactTextsCaseSensitiveInAnyOrder : ExactTexts {
    constructor(vararg exactTexts: String) : super(*exactTexts)
    constructor(exactTexts: List<String>) : super(exactTexts)
    override suspend fun test(input: List<WebElement>): Boolean {
        if (input.size != expectedTexts.size) {
            return false
        }
        val elementsTexts = input.map { obj: WebElement -> obj.getText() }
        for (expectedText in expectedTexts) {
            var found = false
            for (elementText in elementsTexts) {
                if (Html.text.equalsCaseSensitive(elementText, expectedText)) {
                    found = true
                }
            }
            if (!found) {
                return false
            }
        }
        return true
    }

    override fun toString(): String {
        return "Exact texts case sensitive in any order $expectedTexts"
    }
}

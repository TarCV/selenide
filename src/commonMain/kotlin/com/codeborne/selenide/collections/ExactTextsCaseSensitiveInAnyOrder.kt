package com.codeborne.selenide.collections

import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement

class ExactTextsCaseSensitiveInAnyOrder : ExactTexts {
    constructor(vararg exactTexts: String) : super(*exactTexts)
    constructor(exactTexts: List<String>) : super(exactTexts)
    override fun test(elements: List<org.openqa.selenium.WebElement>): Boolean {
        if (elements.size != expectedTexts.size) {
            return false
        }
        val elementsTexts = elements.map { obj: org.openqa.selenium.WebElement -> obj.text }
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

package com.codeborne.selenide.collections

import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement

class TextsInAnyOrder : ExactTexts {
    constructor(vararg expectedTexts: String) : super(*expectedTexts)
    constructor(expectedTexts: List<String>) : super(expectedTexts)
    override fun test(elements: List<org.openqa.selenium.WebElement>): Boolean {
        if (elements.size != expectedTexts.size) {
            return false
        }
        val elementsTexts = elements.map { obj: org.openqa.selenium.WebElement -> obj.text }
        for (expectedText in expectedTexts) {
            var found = false
            for (elementText in elementsTexts) {
                if (Html.text.contains(elementText, expectedText)) {
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
        return "TextsInAnyOrder $expectedTexts"
    }
}

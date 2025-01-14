package com.codeborne.selenide.collections

import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement

class Texts : ExactTexts {
    constructor(vararg expectedTexts: String) : super(*expectedTexts)
    constructor(expectedTexts: List<String>) : super(expectedTexts)
    override suspend fun test(elements: List<org.openqa.selenium.WebElement>): Boolean {
        if (elements.size != expectedTexts.size) {
            return false
        }
        for (i in expectedTexts.indices) {
            val element = elements[i]
            val expectedText = expectedTexts[i]
            if (!Html.text.contains(element.getText(), expectedText)) {
                return false
            }
        }
        return true
    }

    override fun toString(): String {
        return "texts $expectedTexts"
    }
}

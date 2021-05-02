package com.codeborne.selenide.collections

import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement

class Texts : ExactTexts {
    constructor(vararg expectedTexts: String) : super(*expectedTexts)
    constructor(expectedTexts: List<String>) : super(expectedTexts)
    override operator fun invoke(elements: List<WebElement>): Boolean {
        if (elements.size != expectedTexts.size) {
            return false
        }
        for (i in expectedTexts.indices) {
            val element = elements[i]
            val expectedText = expectedTexts[i]
            if (!Html.text.contains(element.text, expectedText)) {
                return false
            }
        }
        return true
    }

    override fun toString(): String {
        return "texts $expectedTexts"
    }
}

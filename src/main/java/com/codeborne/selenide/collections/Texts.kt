package com.codeborne.selenide.collections

import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Texts : ExactTexts {
    constructor(vararg expectedTexts: String) : super(*expectedTexts) {}
    constructor(expectedTexts: List<String>) : super(expectedTexts) {}

    @CheckReturnValue
    override fun test(elements: List<WebElement>): Boolean {
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

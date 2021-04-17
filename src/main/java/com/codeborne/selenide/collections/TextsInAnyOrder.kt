package com.codeborne.selenide.collections

import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement
import java.util.stream.Collectors
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class TextsInAnyOrder : ExactTexts {
    constructor(vararg expectedTexts: String) : super(*expectedTexts) {}
    constructor(expectedTexts: List<String>) : super(expectedTexts) {}

    @CheckReturnValue
    override fun test(elements: List<WebElement>): Boolean {
        if (elements.size != expectedTexts.size) {
            return false
        }
        val elementsTexts = elements.stream().map { obj: WebElement -> obj.text }.collect(Collectors.toList())
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

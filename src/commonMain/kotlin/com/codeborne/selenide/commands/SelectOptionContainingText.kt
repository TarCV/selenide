package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Quotes
import org.openqa.selenium.support.ui.Select

class SelectOptionContainingText : Command<Unit> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): Unit {
        val text = Util.firstOf<String>(args)
        val element = locator.getWebElement()
        val select = Select(element)
        val options = element.findElements(
            org.openqa.selenium.By.xpath(
                ".//option[contains(normalize-space(.), " + Quotes.escape(text) + ")]"
            )
        )
        if (options.isEmpty()) {
            throw org.openqa.selenium.NoSuchElementException("Cannot locate option containing text: $text")
        }
        for (option in options) {
            setSelected(option)
            if (!select.isMultiple) {
                break
            }
        }
    }

    private suspend fun setSelected(option: org.openqa.selenium.WebElement) {
        if (!option.isSelected) {
            option.click()
        }
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Quotes
import org.openqa.selenium.support.ui.Select

class SelectOptionContainingText : Command<Nothing?> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): Nothing? {
        val text = Util.firstOf<String>(args)
        val element = locator.getWebElement()
        val select = Select(element)
        val options = element.findElements(
            By.xpath(
                ".//option[contains(normalize-space(.), " + Quotes.escape(text) + ")]"
            )
        )
        if (options.isEmpty()) {
            throw NoSuchElementException("Cannot locate option containing text: $text")
        }
        for (option in options) {
            setSelected(option)
            if (!select.isMultiple) {
                break
            }
        }
        return null
    }

    private suspend fun setSelected(option: WebElement) {
        if (!option.isSelected) {
            option.click()
        }
    }
}

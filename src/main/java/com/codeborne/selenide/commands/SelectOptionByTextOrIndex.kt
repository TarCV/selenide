package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Condition
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.support.ui.Select
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SelectOptionByTextOrIndex : Command<Void?> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>?): Void? {
        require(!(args == null || args.isEmpty())) { "Missing arguments" }
      val firstArg = args[0]
      if (firstArg is Array<*> && firstArg[0] is String) { // TODO: why check in Java code was incomplete?
            selectOptionsByTexts(locator, firstArg as Array<String>)
        } else if (firstArg is IntArray) {
            selectOptionsByIndexes(locator, firstArg)
        }
        return null
    }

    private fun selectOptionsByTexts(selectField: WebElementSource, texts: Array<String>) {
        val select = Select(selectField.webElement)
        for (text in texts) {
            try {
                select.selectByVisibleText(text)
            } catch (e: NoSuchElementException) {
                throw ElementNotFound(
                    selectField.driver(),
                    selectField.description() + "/option[text:" + text + ']',
                    Condition.exist,
                    e
                )
            }
        }
    }

    private fun selectOptionsByIndexes(selectField: WebElementSource, indexes: IntArray) {
        val select = Select(selectField.webElement)
        for (index in indexes) {
            try {
                select.selectByIndex(index)
            } catch (e: NoSuchElementException) {
                throw ElementNotFound(
                    selectField.driver(),
                    selectField.description() + "/option[index:" + index + ']',
                    Condition.exist,
                    e
                )
            }
        }
    }
}

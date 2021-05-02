package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Condition
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.support.ui.Select

class SelectOptionByValue : Command<Nothing?> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): Nothing? {
        val select = Select(locator.getWebElement())
        require(!(args == null || args.isEmpty())) { "Missing arguments" }
      val firstArg = args[0]
      if (firstArg is String) {
            selectOptionByValue(locator, select, firstArg)
        } else if (firstArg is Array<*> && firstArg[0] is String) { // TODO: why check in Java code was incomplete?
            val values = firstArg as Array<String>
            for (value in values) {
                selectOptionByValue(locator, select, value)
            }
        }
        return null
    }

    private suspend fun selectOptionByValue(selectField: WebElementSource, select: Select, value: String) {
        try {
            select.selectByValue(value)
        } catch (e: NoSuchElementException) {
            throw ElementNotFound(
                selectField.driver(),
                selectField.description() + "/option[value:" + value + ']',
                Condition.exist,
                e
            )
        }
    }
}

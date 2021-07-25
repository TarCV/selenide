package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.SelenideElementProxy.Companion.NO_ARGS
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement

class GetSelectedText : Command<String> {
    private val getSelectedOption: GetSelectedOption

    internal constructor(getSelectedOption: GetSelectedOption) {
        this.getSelectedOption = getSelectedOption
    }

    constructor() {
        getSelectedOption = GetSelectedOption()
    }
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): String {
        val option: org.openqa.selenium.WebElement = getSelectedOption.execute(proxy, locator, NO_ARGS)
        return option.getText()
    }
}

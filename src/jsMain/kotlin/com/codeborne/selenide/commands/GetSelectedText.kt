package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
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
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): String {
        val option: WebElement = getSelectedOption.execute(proxy, locator, Command.NO_ARGS)
        return option.text
    }
}
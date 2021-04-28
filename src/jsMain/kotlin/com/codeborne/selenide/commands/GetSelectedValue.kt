package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement
import okio.IOException

class GetSelectedValue : Command<String?> {
    private val getSelectedOption: Command<SelenideElement>

    constructor() {
        getSelectedOption = GetSelectedOption()
    }

    constructor(getSelectedOption: Command<SelenideElement>) {
        this.getSelectedOption = getSelectedOption
    }
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): String? {
        val option: WebElement? = getSelectedOption.execute(proxy, locator, args)
        return option?.getAttribute("value")
    }
}

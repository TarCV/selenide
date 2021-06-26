package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class Append : Command<SelenideElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        val input = locator.getWebElement()
        input.sendKeys(Util.firstOf<Any>(args) as String)
        return proxy
    }
}

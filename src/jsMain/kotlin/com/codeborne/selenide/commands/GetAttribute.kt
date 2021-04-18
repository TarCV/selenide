package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class GetAttribute : Command<String?> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): String? {
        val attributeName = Util.firstOf<String>(args)
        return locator.getWebElement().getAttribute(attributeName)
    }
}

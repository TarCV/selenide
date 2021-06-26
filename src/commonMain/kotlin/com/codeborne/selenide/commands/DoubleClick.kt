package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class DoubleClick : Command<SelenideElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        locator.driver().actions().doubleClick(locator.findAndAssertElementIsInteractable()).perform()
        return proxy
    }
}

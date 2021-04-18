package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class ContextClick : Command<SelenideElement> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>?): SelenideElement {
        locator.driver().actions().contextClick(locator.findAndAssertElementIsInteractable()).perform()
        return proxy
    }
}

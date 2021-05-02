package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.Keys

class PressEscape : Command<SelenideElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        locator.findAndAssertElementIsInteractable().sendKeys(org.openqa.selenium.Keys.ESCAPE)
        return proxy
    }
}

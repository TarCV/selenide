package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.interactions.Actions

class Hover : Command<SelenideElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        val element = locator.getWebElement()
        org.openqa.selenium.interactions.Actions(locator.driver().webDriver).moveToElement(element).perform()
        return proxy
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement

class GetWrappedElement : Command<WebElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): WebElement {
        return locator.getWebElement()
    }
}

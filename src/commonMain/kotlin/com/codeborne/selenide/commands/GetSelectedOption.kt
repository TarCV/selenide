package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import com.codeborne.selenide.impl.WebElementWrapper
import org.openqa.selenium.support.ui.Select

class GetSelectedOption : Command<SelenideElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        return WebElementWrapper.wrap(locator.driver(), Select(locator.getWebElement()).firstSelectedOption)
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import com.codeborne.selenide.impl.WebElementWrapper
import org.openqa.selenium.support.ui.Select
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class GetSelectedOption : Command<SelenideElement> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        return WebElementWrapper.wrap(locator.driver(), Select(locator.webElement).firstSelectedOption)
    }
}

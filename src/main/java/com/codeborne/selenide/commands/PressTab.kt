package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.Keys
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class PressTab : Command<SelenideElement> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>?): SelenideElement {
        locator.findAndAssertElementIsInteractable().sendKeys(Keys.TAB)
        return proxy
    }
}

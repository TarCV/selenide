package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.interactions.Actions
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Hover : Command<SelenideElement> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>?): SelenideElement {
        val element = locator.webElement
        Actions(locator.driver().webDriver).moveToElement(element).perform()
        return proxy
    }
}

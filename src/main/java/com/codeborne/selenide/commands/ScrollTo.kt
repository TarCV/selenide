package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ScrollTo : Command<SelenideElement> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>?): SelenideElement {
        val location = locator.webElement.location
        locator.driver().executeJavaScript<Any>("window.scrollTo(" + location.getX() + ", " + location.getY() + ')')
        return proxy
    }
}

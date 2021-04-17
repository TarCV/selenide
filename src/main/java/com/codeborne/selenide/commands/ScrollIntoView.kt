package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ScrollIntoView : Command<SelenideElement> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<Any>?): SelenideElement {
        val param = Util.firstOf<Any>(args)
        locator.driver().executeJavaScript<Any>("arguments[0].scrollIntoView($param)", proxy)
        return proxy
    }
}

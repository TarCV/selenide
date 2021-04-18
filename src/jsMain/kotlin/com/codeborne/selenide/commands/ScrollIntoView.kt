package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class ScrollIntoView : Command<SelenideElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        val param = Util.firstOf<Any>(args)
        locator.driver().executeJavaScript<Any>("arguments[0].scrollIntoView($param)", proxy)
        return proxy
    }
}

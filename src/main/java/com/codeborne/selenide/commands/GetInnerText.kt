package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class GetInnerText : Command<String> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<Any>?): String {
        val element = locator.webElement
        return element.getAttribute("textContent")
    }
}
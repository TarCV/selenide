package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class As : Command<SelenideElement> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        locator.setAlias(checkNotNull(args)[0] as String)
        return proxy
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class Should protected constructor(private val prefix: String) : Command<SelenideElement> {
    constructor() : this("") {}

    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        for (condition in Util.argsToConditions(args)) {
            locator.checkCondition(prefix, condition, false)
        }
        return proxy
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

open class ShouldNot protected constructor(private val prefix: String) : SoftAssertionCommand {
    constructor() : this("")

    @kotlin.time.ExperimentalTime
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        for (condition in Util.argsToConditions(args)) {
            locator.checkCondition(prefix, condition, true)
        }
        return proxy
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.SelenideElementProxy.Companion.NO_ARGS
import com.codeborne.selenide.impl.WebElementSource

class GetVal : Command<String?> {
    private val getValue: GetValue

    constructor() {
        getValue = GetValue()
    }

    constructor(getValue: GetValue) {
        this.getValue = getValue
    }

    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): String? {
        return getValue.execute(proxy, locator, NO_ARGS)
    }
}

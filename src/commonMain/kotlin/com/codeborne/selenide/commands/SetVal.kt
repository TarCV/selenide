package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class SetVal : Command<SelenideElement> {
    private val setValue: SetValue

    constructor() {
        setValue = SetValue()
    }

    constructor(setValue: SetValue) {
        this.setValue = setValue
    }

    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        setValue.execute(proxy, locator, args)
        return proxy
    }
}

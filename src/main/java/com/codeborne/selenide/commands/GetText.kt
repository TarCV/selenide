package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.CheckReturnValue

class GetText : Command<String> {
    private val getSelectedText: GetSelectedText

    constructor() {
        getSelectedText = GetSelectedText()
    }

    constructor(getSelectedtext: GetSelectedText) {
        getSelectedText = getSelectedtext
    }

    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>?): String {
        val element = locator.webElement
        return if ("select".equals(element.tagName, ignoreCase = true)) getSelectedText.execute(
            proxy,
            locator,
            args
        ) else element.text
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class GetAlias : CommandSync<String?> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): String? {
        return locator.alias.text
    }
}

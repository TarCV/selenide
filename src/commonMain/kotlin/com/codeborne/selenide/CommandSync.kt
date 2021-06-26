package com.codeborne.selenide

import com.codeborne.selenide.impl.WebElementSource

interface CommandSync<out T> {
    fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): T
}

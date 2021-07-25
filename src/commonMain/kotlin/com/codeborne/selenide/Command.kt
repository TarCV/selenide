package com.codeborne.selenide

import com.codeborne.selenide.impl.WebElementSource

interface Command<out T> {
    suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): T
}

package com.codeborne.selenide

import com.codeborne.selenide.impl.WebElementSource

interface Command<T> {
    suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): T?

    companion object {
        val NO_ARGS = emptyArray<Any>()
    }
}

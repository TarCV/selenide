package com.codeborne.selenide

import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.runBlocking

interface Command<T> {
    suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): T?

    fun executeBlocking(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): T? = runBlocking {
        execute(proxy, locator, args)
    }

    companion object {
        val NO_ARGS = emptyArray<Any>()
    }
}

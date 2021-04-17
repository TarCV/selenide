package com.codeborne.selenide

import com.codeborne.selenide.impl.WebElementSource
import java.io.IOException
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
interface Command<T> {
    @Throws(IOException::class)
    fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<Any>?): T?

    companion object {
        @JvmField
        val NO_ARGS = emptyArray<Any>()
    }
}

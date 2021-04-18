package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import okio.okio.IOException

class Execute<ReturnType> : Command<ReturnType?> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): ReturnType? {
        val command: Command<ReturnType> = Util.firstOf(args)
        return try {
            command.execute(proxy, locator, args)
        } catch (e: IOException) {
            throw RuntimeException("Unable to execute custom command", e)
        }
    }
}

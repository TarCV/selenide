package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class GetPseudoValue : Command<String> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>?): String {
        val pseudoElement = Util.firstOf<String>(args)
        if (args!!.size > 1) {
            val propertyName = args[1] as String
            return locator.driver().executeJavaScript<String>(JS_CODE, locator.webElement, pseudoElement, propertyName)
        }
        return locator.driver().executeJavaScript<String>(JS_CODE, locator.webElement, pseudoElement, "content")
    }

    companion object {
        const val JS_CODE = "return window.getComputedStyle(arguments[0], arguments[1])" +
                ".getPropertyValue(arguments[2]);"
    }
}

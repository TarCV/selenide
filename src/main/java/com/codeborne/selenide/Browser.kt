package com.codeborne.selenide

import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Browser(val name: String, @get:CheckReturnValue val isHeadless: Boolean) {

    @get:CheckReturnValue
    val isChrome: Boolean
        get() = Browsers.CHROME.equals(name, ignoreCase = true)


    @CheckReturnValue
    fun supportsInsecureCerts(): Boolean {
        return false
    }
}

package com.codeborne.selenide

import com.Browsers


class Browser(val name: String, val isHeadless: Boolean) {
    val isChrome: Boolean
        get() = Browsers.CHROME.equals(name, ignoreCase = true)
    fun supportsInsecureCerts(): Boolean {
        return true
    }
}

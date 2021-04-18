package com.codeborne.selenide

class ClipboardService {
    fun getClipboard(driver: Driver): Clipboard {
        return DefaultClipboard(driver)
    }
}

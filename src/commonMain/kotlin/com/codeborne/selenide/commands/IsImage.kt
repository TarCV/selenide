package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.conditions.IsImageLoaded
import com.codeborne.selenide.impl.WebElementSource

class IsImage : Command<Boolean> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): Boolean {
        val img = locator.getWebElement()
        require("img".equals(img.tagName, ignoreCase = true)) { "Method isImage() is only applicable for img elements" }
        return IsImageLoaded.isImage(locator.driver(), img)
    }
}

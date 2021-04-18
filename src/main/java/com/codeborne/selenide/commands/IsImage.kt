package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.conditions.IsImageLoaded
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class IsImage : Command<Boolean> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): Boolean {
        val img = locator.webElement
        require("img".equals(img.tagName, ignoreCase = true)) { "Method isImage() is only applicable for img elements" }
        return IsImageLoaded.isImage(locator.driver(), img)
    }
}

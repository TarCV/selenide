package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By

class GetPreceding : Command<SelenideElement?> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        checkNotNull(args)
        val siblingIndex = Util.firstOf<Any>(args) as Int + 1
        return locator.find(proxy, By.xpath("preceding-sibling::*[${siblingIndex}]"), 0)
    }
}
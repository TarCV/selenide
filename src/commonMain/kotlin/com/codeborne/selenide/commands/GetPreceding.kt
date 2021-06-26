package com.codeborne.selenide.commands

import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class GetPreceding : CommandSync<SelenideElement> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        checkNotNull(args)
        val siblingIndex = Util.firstOf<Any>(args) as Int + 1
        return locator.find(proxy, org.openqa.selenium.By.xpath("preceding-sibling::*[${siblingIndex}]"), 0)
    }
}

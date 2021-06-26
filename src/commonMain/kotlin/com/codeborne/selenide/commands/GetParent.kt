package com.codeborne.selenide.commands

import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class GetParent : CommandSync<SelenideElement> {
    private val find: Find

    internal constructor() {
        find = Find()
    }

    internal constructor(find: Find) {
        this.find = find
    }
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        return find.execute(proxy, locator, arrayOf(org.openqa.selenium.By.xpath(".."), 0))
    }
}

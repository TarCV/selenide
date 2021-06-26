package com.codeborne.selenide.commands

import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class GetLastChild : CommandSync<SelenideElement> {
    private val find: Find

    constructor() {
        find = Find()
    }

    constructor(find: Find) {
        this.find = find
    }
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        return find.execute(proxy, locator, arrayOf(org.openqa.selenium.By.xpath("*[last()]"), 0))
    }
}

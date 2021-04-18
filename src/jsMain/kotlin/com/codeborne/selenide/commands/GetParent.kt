package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By

class GetParent : Command<SelenideElement> {
    private val find: Find

    internal constructor() {
        find = Find()
    }

    internal constructor(find: Find) {
        this.find = find
    }
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        return find.execute(proxy, locator, arrayOf(By.xpath(".."), 0))
    }
}

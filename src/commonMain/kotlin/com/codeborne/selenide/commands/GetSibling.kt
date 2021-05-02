package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By

class GetSibling : Command<SelenideElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        val siblingIndex = Util.firstOf<Any>(args) as Int + 1
        return locator.find(proxy, org.openqa.selenium.By.xpath("following-sibling::*[${siblingIndex}]"), 0)
    }
}

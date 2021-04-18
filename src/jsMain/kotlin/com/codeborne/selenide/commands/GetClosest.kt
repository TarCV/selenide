package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By

class GetClosest : Command<SelenideElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        val tagOrClass = Util.firstOf<String>(args)
        val xpath = if (tagOrClass.startsWith("."))
            "ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' ${tagOrClass.substring(1)} ')][1]"
        else "ancestor::${tagOrClass}[1]"
        return locator.find(proxy, By.xpath(xpath), 0)
    }
}

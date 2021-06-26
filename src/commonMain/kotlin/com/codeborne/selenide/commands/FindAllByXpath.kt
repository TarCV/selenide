package com.codeborne.selenide.commands

import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.BySelectorCollection
import com.codeborne.selenide.impl.WebElementSource

class FindAllByXpath : CommandSync<ElementsCollection> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): ElementsCollection {
        val xpath = Util.firstOf<String>(args)
        return ElementsCollection(BySelectorCollection(locator.driver(), proxy, org.openqa.selenium.By.xpath(xpath)))
    }
}

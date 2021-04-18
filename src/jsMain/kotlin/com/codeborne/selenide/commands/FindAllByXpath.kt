package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.BySelectorCollection
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By

class FindAllByXpath : Command<ElementsCollection?> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): ElementsCollection {
        val xpath = Util.firstOf<String>(args)
        return ElementsCollection(BySelectorCollection(locator.driver(), proxy, By.xpath(xpath)))
    }
}

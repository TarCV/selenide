package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.BySelectorCollection
import com.codeborne.selenide.impl.WebElementSource

class FindAll : Command<ElementsCollection> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): ElementsCollection {
        val selector = Util.firstOf<Any>(args)
        return ElementsCollection(
            BySelectorCollection(locator.driver(), proxy, WebElementSource.getSelector(selector))
        )
    }
}

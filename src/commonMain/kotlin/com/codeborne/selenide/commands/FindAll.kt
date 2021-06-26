package com.codeborne.selenide.commands

import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.BySelectorCollection
import com.codeborne.selenide.impl.WebElementSource

class FindAll : CommandSync<ElementsCollection> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): ElementsCollection {
        val selector = Util.firstOf<Any>(args)
        return ElementsCollection(
            BySelectorCollection(locator.driver(), proxy, WebElementSource.getSelector(selector))
        )
    }
}

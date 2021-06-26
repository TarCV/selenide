package com.codeborne.selenide.commands

import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class FindByXpath : CommandSync<SelenideElement> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        checkNotNull(args)

        val xpath = Util.firstOf<String>(args)
        val byXpath = org.openqa.selenium.By.xpath(xpath)
        return if (args.size == 1) locator.find(proxy, byXpath, 0) else locator.find(
            proxy,
            byXpath,
            args[1] as Int
        )
    }
}

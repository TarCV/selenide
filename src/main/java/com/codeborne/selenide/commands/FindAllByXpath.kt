package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.BySelectorCollection
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class FindAllByXpath : Command<ElementsCollection?> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): ElementsCollection {
        val xpath = Util.firstOf<String>(args)
        return ElementsCollection(BySelectorCollection(locator.driver(), proxy, By.xpath(xpath)))
    }
}

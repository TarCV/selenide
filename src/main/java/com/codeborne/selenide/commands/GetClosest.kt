package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class GetClosest : Command<SelenideElement> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>?): SelenideElement {
        val tagOrClass = Util.firstOf<String>(args)
        val xpath = if (tagOrClass.startsWith(".")) String.format(
            "ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' %s ')][1]",
            tagOrClass.substring(1)
        ) else String.format("ancestor::%s[1]", tagOrClass)
        return locator.find(proxy, By.xpath(xpath), 0)
    }
}

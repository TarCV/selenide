package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class GetPreceding : Command<SelenideElement?> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>?): SelenideElement {
        checkNotNull(args)
        val siblingIndex = Util.firstOf<Any>(args) as Int + 1
        return locator.find(proxy, By.xpath(String.format("preceding-sibling::*[%d]", siblingIndex)), 0)
    }
}

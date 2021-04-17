package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class FindByXpath : Command<SelenideElement> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<Any>?): SelenideElement {
        checkNotNull(args)

        val xpath = Util.firstOf<String>(args)
        val byXpath = By.xpath(xpath)
        return if (args.size == 1) locator.find(proxy, byXpath, 0) else locator.find(
            proxy,
            byXpath,
            (args[1] as Int?)!!
        )
    }
}

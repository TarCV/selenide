package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class GetWrappedElement : Command<WebElement> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>?): WebElement {
        return locator.webElement
    }
}

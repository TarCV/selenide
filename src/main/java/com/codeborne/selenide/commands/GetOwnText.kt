package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class GetOwnText : Command<String> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): String {
        return getOwnText(locator.driver(), locator.webElement)
    }

    companion object {
        @JvmStatic
        fun getOwnText(driver: Driver, element: WebElement): String {
            return driver.executeJavaScript(
                """return Array.prototype.filter.call(arguments[0].childNodes, function (element) {
  return element.nodeType === Node.TEXT_NODE;
}).map(function (element) {
  return element.textContent;
}).join("\n");""", element
            )
        }
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebDriverException

class ToString : Command<String> {
    private val describe = Plugins.injectA(
        ElementDescriber::class
    )
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): String {
        return try {
            describe.fully(locator.driver(), locator.getWebElement())
        } catch (elementDoesNotExist: org.openqa.selenium.WebDriverException) {
            Cleanup.of.webdriverExceptionMessage(elementDoesNotExist)
        } catch (elementDoesNotExist: ElementNotFound) {
            elementDoesNotExist.message.toString()
        } catch (elementDoesNotExist: IndexOutOfBoundsException) {
            elementDoesNotExist.toString()
        }
    }
}

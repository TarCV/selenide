package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebDriverException

class Exists : Command<Boolean> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): Boolean {
        return try {
            locator.getWebElement()
            true
        } catch (elementNotFound: WebDriverException) {
            if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
                throw Cleanup.of.wrap(elementNotFound)
            }
            false
        } catch (elementNotFound: ElementNotFound) {
            if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
                throw Cleanup.of.wrap(elementNotFound)
            }
            false
        } catch (invalidElementIndex: IndexOutOfBoundsException) {
            false
        }
    }
}

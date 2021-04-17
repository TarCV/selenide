package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Condition
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Matches : Command<Boolean> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<Any>?): Boolean {
        val condition = Util.firstOf<Condition>(args)
        val element = getElementOrNull(locator)
        return if (element != null) {
            condition.apply(locator.driver(), element)
        } else condition.applyNull()
    }

    @CheckReturnValue
    protected fun getElementOrNull(locator: WebElementSource): WebElement? {
        return try {
            locator.webElement
        } catch (elementNotFound: WebDriverException) {
            if (Cleanup.of.isInvalidSelectorError(elementNotFound)) throw Cleanup.of.wrap(elementNotFound)
            null
        } catch (elementNotFound: ElementNotFound) {
            if (Cleanup.of.isInvalidSelectorError(elementNotFound)) throw Cleanup.of.wrap(elementNotFound)
            null
        } catch (ignore: IndexOutOfBoundsException) {
            null
        } catch (e: RuntimeException) {
            throw Cleanup.of.wrap(e)
        }
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebDriverException
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ToString : Command<String> {
    private val describe = Plugins.inject(
        ElementDescriber::class.java
    )

    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): String {
        return try {
            describe.fully(locator.driver(), locator.webElement)
        } catch (elementDoesNotExist: WebDriverException) {
            Cleanup.of.webdriverExceptionMessage(elementDoesNotExist)
        } catch (elementDoesNotExist: ElementNotFound) {
            elementDoesNotExist.message.toString()
        } catch (elementDoesNotExist: IndexOutOfBoundsException) {
            elementDoesNotExist.toString()
        }
    }
}

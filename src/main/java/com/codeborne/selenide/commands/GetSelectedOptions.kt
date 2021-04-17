package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.Alias
import com.codeborne.selenide.impl.CollectionSource
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import javax.annotation.CheckReturnValue

class GetSelectedOptions : Command<ElementsCollection?> {
    override fun execute(
        proxy: SelenideElement,
        locator: WebElementSource,
        args: Array<out Any?>?
    ): ElementsCollection {
        return ElementsCollection(SelectedOptionsCollection(locator))
    }

    private class SelectedOptionsCollection constructor(private val selectElement: WebElementSource) :
        CollectionSource {
        private var alias = Alias.NONE
        @CheckReturnValue
        override fun getElements(): List<WebElement> {
            return select(selectElement).allSelectedOptions
        }

        @CheckReturnValue
        override fun getElement(index: Int): WebElement {
            return if (index == 0) select(selectElement).firstSelectedOption else select(selectElement).allSelectedOptions[index]
        }

        @CheckReturnValue
        private fun select(selectElement: WebElementSource): Select {
            return Select(selectElement.webElement)
        }

        @CheckReturnValue
        override fun description(): String {
            return alias.getOrElse { selectElement.description() + " selected options" }
        }

        @CheckReturnValue
        override fun driver(): Driver {
            return selectElement.driver()
        }

        override fun setAlias(alias: String) {
            this.alias = Alias(alias)
        }
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Condition.Companion.value
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.InvalidStateException
import com.codeborne.selenide.impl.WebElementSource
import com.codeborne.selenide.impl.WebElementWrapper

class SelectRadio : Command<SelenideElement> {
    private val click: Click

    constructor() {
        click = Click()
    }

    constructor(click: Click) {
        this.click = click
    }

    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        val value = Util.firstOf<String>(args)
        val matchingRadioButtons = locator.findAll()
        for (radio in matchingRadioButtons) {
            if (value == radio.getAttribute("value")) {
                if (radio.getAttribute("readonly") != null) throw InvalidStateException(
                    locator.driver(),
                    "Cannot select readonly radio button"
                )
                click.click(locator.driver(), radio)
                return WebElementWrapper.wrap(locator.driver(), radio)
            }
        }
        throw ElementNotFound(locator.driver(), locator.description(), value(value))
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.InvalidStateException
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SetSelected : Command<SelenideElement> {
    private val click: Click

    constructor() {
        click = Click()
    }

    constructor(click: Click) {
        this.click = click
    }

    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<Any>?): SelenideElement {
        val selected = Util.firstOf<Boolean>(args)
        val element = locator.webElement
        if (!element.isDisplayed) {
            throw InvalidStateException(locator.driver(), "Cannot change invisible element")
        }
        val tag = element.tagName
        if (tag != "option") {
            if (tag == "input") {
                val type = element.getAttribute("type")
                if (type != "checkbox" && type != "radio") {
                    throw InvalidStateException(locator.driver(), "Only use setSelected on checkbox/option/radio")
                }
            } else {
                throw InvalidStateException(locator.driver(), "Only use setSelected on checkbox/option/radio")
            }
        }
        if (element.getAttribute("readonly") != null || element.getAttribute("disabled") != null) {
            throw InvalidStateException(locator.driver(), "Cannot change value of readonly/disabled element")
        }
        if (element.isSelected != selected) {
            click.execute(proxy, locator, Command.NO_ARGS)
        }
        return proxy
    }
}

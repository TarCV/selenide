package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.InvalidStateException
import com.codeborne.selenide.impl.Events
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SetValue : Command<SelenideElement> {
    private val selectOptionByValue: SelectOptionByValue
    private val selectRadio: SelectRadio

    constructor() {
        selectOptionByValue = SelectOptionByValue()
        selectRadio = SelectRadio()
    }

    constructor(selectOptionByValue: SelectOptionByValue, selectRadio: SelectRadio) {
        this.selectOptionByValue = selectOptionByValue
        this.selectRadio = selectRadio
    }

    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>?): SelenideElement {
        val text = Util.firstOf<String>(args)
        val element = locator.findAndAssertElementIsInteractable()
        if (locator.driver().config().versatileSetValue()
            && "select".equals(element.tagName, ignoreCase = true)
        ) {
            selectOptionByValue.execute(proxy, locator, args)
            return proxy
        }
        if (locator.driver().config().versatileSetValue()
            && "input".equals(element.tagName, ignoreCase = true) && "radio" == element.getAttribute("type")
        ) {
            selectRadio.execute(proxy, locator, args)
            return proxy
        }
        setValueForTextInput(locator.driver(), element, text)
        return proxy
    }

    private fun setValueForTextInput(driver: Driver, element: WebElement, text: String?) {
        if (text == null || text.isEmpty()) {
            element.clear()
        } else if (driver.config().fastSetValue()) {
            val error = setValueByJs(driver, element, text)
            if (error != null) throw InvalidStateException(driver, error) else {
                Events.events.fireEvent(driver, element, "keydown", "keypress", "input", "keyup", "change")
            }
        } else {
            element.clear()
            element.sendKeys(text)
        }
    }

    private fun setValueByJs(driver: Driver, element: WebElement, text: String): String? {
        return driver.executeJavaScript(
            "return (function(webelement, text) {" +
                    "if (webelement.getAttribute('readonly') != undefined) return 'Cannot change value of readonly element';" +
                    "if (webelement.getAttribute('disabled') != undefined) return 'Cannot change value of disabled element';" +
                    "webelement.focus();" +
                    "var maxlength = webelement.getAttribute('maxlength') == null ? -1 : parseInt(webelement.getAttribute('maxlength'));" +
                    "webelement.value = " +
                    "maxlength == -1 ? text " +
                    ": text.length <= maxlength ? text " +
                    ": text.substring(0, maxlength);" +
                    "return null;" +
                    "})(arguments[0], arguments[1]);",
            element, text
        )
    }
}

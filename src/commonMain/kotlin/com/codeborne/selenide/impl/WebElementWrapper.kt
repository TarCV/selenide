package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.Plugins.injectA
import org.openqa.selenium.WebElement
import support.reflect.Proxy

class WebElementWrapper(
    private val driver: Driver, private val webElement: org.openqa.selenium.WebElement
) : WebElementSource() {
    private val describe = injectA(ElementDescriber::class)
    override suspend fun getSearchCriteria(): String { // TODO: should not be a suspending function
        return describe.briefly(driver, getWebElement())
    }
    override fun toString(): String {
        return alias.getOrElse { webElement.toString() }
    }
    override fun driver(): Driver {
        return driver
    }

    override suspend fun getWebElement(): org.openqa.selenium.WebElement = webElement

    companion object {
        fun wrap(driver: Driver, element: org.openqa.selenium.WebElement): SelenideElement {
            return if (element is SelenideElement) element else (Proxy.newProxyInstance(
                 null, arrayOf(SelenideElement::class),
                SelenideElementProxy(WebElementWrapper(driver, element))
            ) as SelenideElement)
        }
    }
}

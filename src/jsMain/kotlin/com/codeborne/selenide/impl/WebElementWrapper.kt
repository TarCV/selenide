package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.Plugins.inject
import org.openqa.selenium.WebElement
import support.reflect.Proxy

class WebElementWrapper(
    private val driver: Driver, private val webElement: WebElement
) : WebElementSource() {
    private val describe = inject(ElementDescriber::class)
    override val searchCriteria: String
        get() = runBlocking { // TODO: should not be a suspending function
            describe.briefly(driver, getWebElement())
        }
    override fun toString(): String = runBlocking {
        alias.getOrElse { describe.fully(driver(), getWebElement()) }
    }
    override fun driver(): Driver {
        return driver
    }

    override suspend fun getWebElement(): WebElement = webElement

    companion object {
        fun wrap(driver: Driver, element: WebElement): SelenideElement {
            return if (element is SelenideElement) element else (Proxy.newProxyInstance(
                 null, arrayOf<kotlin.reflect.KClass<*>>(SelenideElement::class),
                SelenideElementProxy(WebElementWrapper(driver, element))
            ) as SelenideElement)
        }
    }
}

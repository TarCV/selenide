package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.WebElement

class WebElementWrapper(
    private val driver: Driver,
    private val webElement: org.openqa.selenium.WebElement
) : WebElementSource() {
    override fun getSearchCriteria(): String = "[WebElement]"
    override fun toString(): String {
        return alias.getOrElse { webElement.toString() }
    }
    override fun driver(): Driver {
        return driver
    }

    override suspend fun getWebElement(): org.openqa.selenium.WebElement = webElement

    companion object {
        fun wrap(driver: Driver, element: org.openqa.selenium.WebElement): SelenideElement {
            return if (element is SelenideElement) {
                element
            } else {
                SelenideElement(
                    SelenideElementProxy(WebElementWrapper(driver, element))
                )
            }
        }
    }
}

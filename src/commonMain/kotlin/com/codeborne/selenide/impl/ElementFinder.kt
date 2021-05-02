package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import support.reflect.Proxy

class ElementFinder internal constructor(
    private val driver: Driver,
    private val parent: org.openqa.selenium.SearchContext?,
    private val criteria: org.openqa.selenium.By,
    private val index: Int
) : WebElementSource() {
    private val describe = Plugins.injectA(
        ElementDescriber::class
    )
    override fun find(proxy: SelenideElement, arg: Any, index: Int): SelenideElement {
        return if (arg is org.openqa.selenium.By) wrap(driver, proxy, arg, index) else wrap(
            driver, proxy, org.openqa.selenium.By.cssSelector(arg as String), index
        )
    }
    override fun driver(): Driver {
        return driver
    }

    override suspend fun getWebElement(): org.openqa.selenium.WebElement {
        return if (index == 0) WebElementSelector.instance.findElement(
            driver,
            searchContext,
            criteria
        ) else WebElementSelector.instance.findElements(
            driver, searchContext, criteria
        )[index]
    }
    override suspend fun findAll(): List<org.openqa.selenium.WebElement> {
        return if (index == 0) WebElementSelector.instance.findElements(
            driver(),
            searchContext,
            criteria
        ) else super.findAll()
    }
    private val searchContext: org.openqa.selenium.SearchContext
        get() = if (parent == null) driver().webDriver else if (parent is SelenideElement) parent.toWebElement() else parent
    override suspend fun createElementNotFoundError(condition: Condition, lastError: Throwable?): ElementNotFound {
        if (parent is SelenideElement) {
            parent.should(Condition.exist)
        } else if (parent is org.openqa.selenium.WebElement) {
            WebElementWrapper.wrap(driver(), parent).should(Condition.exist)
        }
        return super.createElementNotFoundError(condition, lastError)
    }

    override suspend fun getSearchCriteria(): String {
        return if (parent == null) elementCriteria() else if (parent is SelenideElement) parent.searchCriteria + "/" + elementCriteria() else elementCriteria()
    }

    private fun elementCriteria(): String {
        return if (index == 0) describe.selector(criteria) else describe.selector(criteria) + '[' + index + ']'
    }
    override fun toString(): String {
        return "{" + alias.getOrElse { "parent: $parent, criteria: $criteria" } + '}'
    }

    companion object {
        fun wrap(driver: Driver, parent: org.openqa.selenium.WebElement?, cssSelector: String): SelenideElement {
            return wrap(driver, parent, org.openqa.selenium.By.cssSelector(cssSelector), 0)
        }

        fun wrap(driver: Driver, cssSelector: String, index: Int): SelenideElement {
            return wrap(driver, null, org.openqa.selenium.By.cssSelector(cssSelector), index)
        }

        fun wrap(driver: Driver, parent: org.openqa.selenium.WebElement, cssSelector: String, index: Int): SelenideElement {
            return wrap(driver, WebElementWrapper.wrap(driver, parent), org.openqa.selenium.By.cssSelector(cssSelector), index)
        }

        fun wrap(driver: Driver, criteria: org.openqa.selenium.By): SelenideElement {
            return wrap(driver, null, criteria, 0)
        }

        fun wrap(driver: Driver, parent: org.openqa.selenium.SearchContext?, criteria: org.openqa.selenium.By, index: Int): SelenideElement {
            return wrap(driver, SelenideElement::class, parent, criteria, index)
        }

        fun <T : SelenideElement> wrap(
            driver: Driver,
            clazz: kotlin.reflect.KClass<T>,
            parent: org.openqa.selenium.SearchContext?,
            criteria: org.openqa.selenium.By,
            index: Int
        ): T {
            return Proxy.newProxyInstance(
                null, arrayOf<kotlin.reflect.KClass<*>>(clazz),
                SelenideElementProxy(ElementFinder(driver, parent, criteria, index))
            ) as T
        }
    }
}

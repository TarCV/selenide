package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement

class BySelectorCollection(private val driver: Driver, private val parent: org.openqa.selenium.SearchContext?, private val selector: org.openqa.selenium.By) :
    CollectionSource {
    private var alias = Alias.NONE

    constructor(driver: Driver, selector: org.openqa.selenium.By) : this(driver, null, selector)

    override suspend fun getElements(): List<org.openqa.selenium.WebElement> {
        val searchContext = parent ?: driver.webDriver
        return WebElementSelector.instance.findElements(driver, searchContext, selector)
    }
    override suspend fun getElement(index: Int): org.openqa.selenium.WebElement {
        val searchContext = parent ?: driver.webDriver
        return if (index == 0) {
            WebElementSelector.instance.findElement(driver, searchContext, selector)
        } else WebElementSelector.instance.findElements(driver, searchContext, selector)[index]
    }
    override suspend fun description(): String {
        return alias.getOrElse { composeDescription() }
    }

    private fun composeDescription(): String {
        return if (parent == null) describe.selector(selector) else if (parent is SelenideElement) parent.searchCriteria + "/" + describe.selector(
            selector
        ) else describe.selector(selector)
    }
    override fun driver(): Driver {
        return driver
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }

    companion object {
        private val describe = Plugins.injectA(
            ElementDescriber::class
        )
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class BySelectorCollection(private val driver: Driver, private val parent: SearchContext?, private val selector: By) :
    CollectionSource {
    private var alias = Alias.NONE

    constructor(driver: Driver, selector: By) : this(driver, null, selector) {}

    @get:CheckReturnValue
    override val elements: List<WebElement>
        get(){
            val searchContext = parent ?: driver.webDriver
            return WebElementSelector.instance.findElements(driver, searchContext, selector)
        }

    @CheckReturnValue
    override fun getElement(index: Int): WebElement {
        val searchContext = parent ?: driver.webDriver
        return if (index == 0) {
            WebElementSelector.instance.findElement(driver, searchContext, selector)
        } else WebElementSelector.instance.findElements(driver, searchContext, selector)[index]
    }

    @CheckReturnValue
    override fun description(): String {
        return alias.getOrElse { composeDescription() }
    }

    private fun composeDescription(): String {
        return if (parent == null) describe.selector(selector) else if (parent is SelenideElement) parent.searchCriteria + "/" + describe.selector(
            selector
        ) else describe.selector(selector)
    }

    @CheckReturnValue
    override fun driver(): Driver {
        return driver
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }

    companion object {
        private val describe = Plugins.inject(
            ElementDescriber::class.java
        )
    }
}

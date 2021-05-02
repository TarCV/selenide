package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class WebElementsCollectionWrapper(private val driver: Driver, elements: Collection<WebElement>) : CollectionSource {
    private val elements = elements.toList()
    override suspend fun getElements(): List<WebElement> = elements

    private var alias = Alias.NONE

    override suspend fun getElement(index: Int): WebElement {
        return getElements()[index]
    }
    override suspend fun description(): String {
        return alias.getOrElseAsync { "$$(" + getElements().size + " elements)" }
    }
    override fun driver(): Driver {
        return driver
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}

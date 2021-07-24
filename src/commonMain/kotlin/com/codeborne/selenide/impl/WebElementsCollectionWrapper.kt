package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class WebElementsCollectionWrapper(private val driver: Driver, elements: Collection<org.openqa.selenium.WebElement>) : CollectionSource {
    private val elements = elements.toList()
    final override suspend fun getElements(): List<org.openqa.selenium.WebElement> = elements

    private var alias = Alias.NONE

    override suspend fun getElement(index: Int): org.openqa.selenium.WebElement {
        return getElements()[index]
    }
    override fun description(): String {
        return alias.getOrElse { "$$(" + elements.size + " elements)" }
    }
    override fun driver(): Driver {
        return driver
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}

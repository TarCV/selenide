package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class CollectionSnapshot(private val originalCollection: CollectionSource) : CollectionSource {
    // TODO: make thread-safe:
    private val _elementsSnapshot: List<WebElement>? = null

    private suspend fun getElementsSnapshot(): List<WebElement> {
        if (_elementsSnapshot == null) {
            _elementsSnapshot = ArrayList(originalCollection.getElements())
        }
        return _elementsSnapshot
    }

    private var alias = Alias.NONE
    override suspend fun getElements(): List<WebElement> {
        return getElementsSnapshot()
    }
    override suspend fun getElement(index: Int): WebElement {
        return getElementsSnapshot()[index]
    }
    override fun description(): String {
        return alias.getOrElse {
            "${originalCollection.description()}.snapshot(${getElementsSnapshot()}.size elements)"
        }
    }
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}

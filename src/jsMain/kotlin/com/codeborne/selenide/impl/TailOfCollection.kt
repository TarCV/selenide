package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class TailOfCollection(private val originalCollection: CollectionSource, private val size: Int) : CollectionSource {
    private var alias = Alias.NONE
    override suspend fun getElements(): List<WebElement> {
        val source = originalCollection.getElements()
        val sourceCollectionSize = source.size
        return source.subList(startingIndex(sourceCollectionSize), sourceCollectionSize)
    }
    override suspend fun getElement(index: Int): WebElement {
        val source = originalCollection.getElements()
        val sourceCollectionSize = source.size
        val startingIndex = startingIndex(sourceCollectionSize)
        return originalCollection.getElement(startingIndex + index)
    }

    private fun startingIndex(sourceCollectionSize: Int): Int {
        return sourceCollectionSize - kotlin.math.min(sourceCollectionSize, size)
    }
    override suspend fun description(): String {
        return alias.getOrElseAsync { originalCollection.description() + ":last(" + size + ')' }
    }
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}

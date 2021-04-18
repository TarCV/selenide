package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue

class TailOfCollection(private val originalCollection: CollectionSource, private val size: Int) : CollectionSource {
    private var alias = Alias.NONE

    @get:CheckReturnValue
    override val elements: List<WebElement>
        get() {
            val source = originalCollection.elements
            val sourceCollectionSize = source.size
            return source.subList(startingIndex(sourceCollectionSize), sourceCollectionSize)
        }

    @CheckReturnValue
    override fun getElement(index: Int): WebElement {
        val source = originalCollection.elements
        val sourceCollectionSize = source.size
        val startingIndex = startingIndex(sourceCollectionSize)
        return originalCollection.getElement(startingIndex + index)
    }

    private fun startingIndex(sourceCollectionSize: Int): Int {
        return sourceCollectionSize - Math.min(sourceCollectionSize, size)
    }

    @CheckReturnValue
    override fun description(): String {
        return alias.getOrElse { originalCollection.description() + ":last(" + size + ')' }
    }

    @CheckReturnValue
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}

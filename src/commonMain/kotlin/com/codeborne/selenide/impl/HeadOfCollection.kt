package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class HeadOfCollection(private val originalCollection: CollectionSource, private val size: Int) : CollectionSource {
    private var alias = Alias.NONE
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    override suspend fun getElements(): List<org.openqa.selenium.WebElement> {
        val source = originalCollection.getElements()
        return source.subList(0, kotlin.math.min(source.size, size))
    }
    override suspend fun getElement(index: Int): org.openqa.selenium.WebElement {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Index: $index, size: $size")
        }
        return originalCollection.getElement(index)
    }
    override suspend fun description(): String {
        return alias.getOrElseAsync { originalCollection.description() + ":first(" + size + ')' }
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}

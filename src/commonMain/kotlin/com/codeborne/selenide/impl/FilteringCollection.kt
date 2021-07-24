package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class FilteringCollection(private val originalCollection: CollectionSource, private val filter: Condition) :
    CollectionSource {
    private var alias = Alias.NONE
    override suspend fun getElements(): List<org.openqa.selenium.WebElement> {
        return originalCollection.getElements()
            .filter { webElement -> filter.apply(originalCollection.driver(), webElement) }
    }
    override suspend fun getElement(index: Int): org.openqa.selenium.WebElement {
        return originalCollection.getElements()
            .filter { webElement -> filter.apply(originalCollection.driver(), webElement) }
            .drop(index)
            .firstOrNull()
            ?: throw IndexOutOfBoundsException("Index: $index")
    }
    override fun description(): String {
        return alias.getOrElse { originalCollection.description() + ".filter(" + filter + ')' }
    }
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import java.util.stream.Collectors
import javax.annotation.CheckReturnValue

class FilteringCollection(private val originalCollection: CollectionSource, private val filter: Condition) :
    CollectionSource {
    private var alias = Alias.NONE

    @get:CheckReturnValue
    override val elements: List<WebElement>
        get() = originalCollection.elements
            .filter { webElement -> filter.apply(originalCollection.driver(), webElement) }

    @CheckReturnValue
    override fun getElement(index: Int): WebElement {
        return originalCollection.elements
            .filter { webElement -> filter.apply(originalCollection.driver(), webElement) }
            .drop(index)
            .firstOrNull()
            ?: throw IndexOutOfBoundsException("Index: $index")
    }

    @CheckReturnValue
    override fun description(): String {
        return alias.getOrElse { originalCollection.description() + ".filter(" + filter + ')' }
    }

    @CheckReturnValue
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}

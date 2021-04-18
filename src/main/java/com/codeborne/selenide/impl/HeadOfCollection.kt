package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue

class HeadOfCollection(private val originalCollection: CollectionSource, private val size: Int) : CollectionSource {
    private var alias = Alias.NONE
    @CheckReturnValue
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    @get:CheckReturnValue
    override val elements: List<WebElement>
        get() {
            val source = originalCollection.elements
            return source.subList(0, Math.min(source.size, size))
        }

    @CheckReturnValue
    override fun getElement(index: Int): WebElement {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Index: $index, size: $size")
        }
        return originalCollection.getElement(index)
    }

    @CheckReturnValue
    override fun description(): String {
        return alias.getOrElse { originalCollection.description() + ":first(" + size + ')' }
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
}

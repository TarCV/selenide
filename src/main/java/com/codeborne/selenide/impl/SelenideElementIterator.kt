package com.codeborne.selenide.impl

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.CollectionElement.Companion.wrap
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class SelenideElementIterator(protected val collection: CollectionSource) : MutableIterator<SelenideElement> {
    @JvmField
    protected var index = 0
    @CheckReturnValue
    override fun hasNext(): Boolean {
        return collection.elements.size > index
    }

    @CheckReturnValue
    override fun next(): SelenideElement {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        return wrap(collection, index++)
    }

    override fun remove() {
        throw UnsupportedOperationException("Cannot remove elements from web page")
    }
}

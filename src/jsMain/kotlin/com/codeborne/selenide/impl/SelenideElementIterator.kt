package com.codeborne.selenide.impl

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.CollectionElement.Companion.wrap

open class SelenideElementIterator(protected val collection: CollectionSource) : MutableIterator<SelenideElement> {
    protected var index = 0
    override fun hasNext(): Boolean = runBlocking { // TODO: should not be blocking
        collection.getElements().size > index
    }
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

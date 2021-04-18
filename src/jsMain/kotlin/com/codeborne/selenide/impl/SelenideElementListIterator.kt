package com.codeborne.selenide.impl

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.CollectionElement.Companion.wrap

class SelenideElementListIterator(collection: CollectionSource, index: Int) : SelenideElementIterator(
  collection
), MutableListIterator<SelenideElement> {
    override fun hasPrevious(): Boolean {
        return index > 0
    }
    override fun previous(): SelenideElement {
        return wrap(collection, --index)
    }
    override fun nextIndex(): Int {
        return index + 1
    }
    override fun previousIndex(): Int {
        return index - 1
    }

    override fun set(element: SelenideElement) {
        throw UnsupportedOperationException("Cannot set elements to web page")
    }

    override fun add(element: SelenideElement) {
        throw UnsupportedOperationException("Cannot add elements to web page")
    }

    init {
        this.index = index
    }
}

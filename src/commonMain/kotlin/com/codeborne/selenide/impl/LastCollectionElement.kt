package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import org.openqa.selenium.WebElement

class LastCollectionElement internal constructor(private val collection: CollectionSource) : WebElementSource() {
    override fun driver(): Driver {
        return collection.driver()
    }

    override suspend fun getWebElement(): org.openqa.selenium.WebElement {
        return lastElementOf(collection.getElements())
    }

    private fun <T> lastElementOf(collection: List<T>): T {
        return collection[collection.size - 1]
    }

    override fun getSearchCriteria(): String {
        return "$collection:last"
    }
    override suspend fun createElementNotFoundError(condition: Condition, lastError: Throwable?): ElementNotFound {
        return if (collection.getElements().isEmpty()) {
            ElementNotFound(
                collection.driver(),
                description(),
                Condition.visible,
                lastError
            )
        } else super.createElementNotFoundError(condition, lastError)
    }

    companion object {
        fun wrap(collection: CollectionSource): SelenideElement {
            return SelenideElement(
                SelenideElementProxy(LastCollectionElement(collection))
            )
        }
    }
}

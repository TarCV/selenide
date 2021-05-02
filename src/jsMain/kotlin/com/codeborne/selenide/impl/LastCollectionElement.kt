package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import org.openqa.selenium.WebElement
import support.reflect.Proxy

class LastCollectionElement internal constructor(private val collection: CollectionSource) : WebElementSource() {
    override fun driver(): Driver {
        return collection.driver()
    }

    override suspend fun getWebElement(): WebElement {
        return lastElementOf(collection.getElements())
    }

    private fun <T> lastElementOf(collection: List<T>): T {
        return collection[collection.size - 1]
    }

    override suspend fun getSearchCriteria(): String {
        return collection.description() + ":last"
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
            return Proxy.newProxyInstance(
                 null, arrayOf<kotlin.reflect.KClass<*>>(SelenideElement::class),
                SelenideElementProxy(LastCollectionElement(collection))
            ) as SelenideElement
        }
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import org.openqa.selenium.WebElement
import support.reflect.Proxy

class CollectionElement internal constructor(private val collection: CollectionSource, private val index: Int) :
    WebElementSource() {
    override fun driver(): Driver {
        return collection.driver()
    }

    override suspend fun getWebElement(): org.openqa.selenium.WebElement {
        return collection.getElement(index)
    }

    override suspend fun getSearchCriteria(): String {
        return collection.description() + '[' + index + ']'
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
        fun wrap(collection: CollectionSource, index: Int): SelenideElement {
            return Proxy.newProxyInstance(
                 null, arrayOf<kotlin.reflect.KClass<*>>(SelenideElement::class),
                SelenideElementProxy(CollectionElement(collection, index))
            ) as SelenideElement
        }
    }
}

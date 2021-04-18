package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import org.openqa.selenium.WebElement
import java.lang.reflect.Proxy
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class LastCollectionElement internal constructor(private val collection: CollectionSource) : WebElementSource() {
    @CheckReturnValue
    override fun driver(): Driver {
        return collection.driver()
    }

    @get:CheckReturnValue
    override val webElement: WebElement
      get() {
          return lastElementOf(collection.elements)
      }

    private fun <T> lastElementOf(collection: List<T>): T {
        return collection[collection.size - 1]
    }

    @get:CheckReturnValue
    override val searchCriteria: String
        get() {
            return collection.description() + ":last"
        }

    @CheckReturnValue
    override fun createElementNotFoundError(condition: Condition, lastError: Throwable?): ElementNotFound {
        return if (collection.elements.isEmpty()) {
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
                collection.javaClass.classLoader, arrayOf<Class<*>>(SelenideElement::class.java),
                SelenideElementProxy(LastCollectionElement(collection))
            ) as SelenideElement
        }
    }
}

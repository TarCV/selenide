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
class CollectionElement internal constructor(private val collection: CollectionSource, private val index: Int) :
    WebElementSource() {
    @CheckReturnValue
    override fun driver(): Driver {
        return collection.driver()
    }

    @get:CheckReturnValue
    override val webElement: WebElement
        get() {
            return collection.getElement(index)
        }

    @get:CheckReturnValue
    override val searchCriteria: String
        get() {
            return collection.description() + '[' + index + ']'
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
        @JvmStatic
        @CheckReturnValue
        fun wrap(collection: CollectionSource, index: Int): SelenideElement {
            return Proxy.newProxyInstance(
                collection.javaClass.classLoader, arrayOf<Class<*>>(SelenideElement::class.java),
                SelenideElementProxy(CollectionElement(collection, index))
            ) as SelenideElement
        }
    }
}

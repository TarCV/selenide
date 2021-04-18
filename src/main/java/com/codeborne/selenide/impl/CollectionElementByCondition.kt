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
class CollectionElementByCondition internal constructor(
    private val collection: CollectionSource,
    private val condition: Condition
) : WebElementSource() {
    @CheckReturnValue
    override fun driver(): Driver {
        return collection.driver()
    }

    @get:CheckReturnValue
    override val webElement: WebElement
    get() {
        val list = collection.elements
        for (element in list) {
            if (condition.apply(driver(), element)) {
                return element
            }
        }
        throw ElementNotFound(driver(), description(), condition)
    }

    @get:CheckReturnValue
    override val searchCriteria: String
        get() {
            return collection.description() + ".findBy(" + condition + ")"
        }

    companion object {
        @JvmStatic
        @CheckReturnValue
        fun wrap(collection: CollectionSource, condition: Condition): SelenideElement {
            return Proxy.newProxyInstance(
                collection.javaClass.classLoader, arrayOf<Class<*>>(SelenideElement::class.java),
                SelenideElementProxy(CollectionElementByCondition(collection, condition))
            ) as SelenideElement
        }
    }
}

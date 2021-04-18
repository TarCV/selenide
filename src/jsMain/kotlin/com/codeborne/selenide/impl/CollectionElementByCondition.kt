package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import org.openqa.selenium.WebElement
import support.reflect.Proxy

class CollectionElementByCondition internal constructor(
    private val collection: CollectionSource,
    private val condition: Condition
) : WebElementSource() {
    override fun driver(): Driver {
        return collection.driver()
    }

    override suspend fun getWebElement(): WebElement = {
        val list = collection.getElements()
        for (element in list) {
            if (condition.apply(driver(), element)) {
                return element
            }
        }
        throw ElementNotFound(driver(), description(), condition)
    }
    override val searchCriteria: String
        get() {
            return collection.description() + ".findBy(" + condition + ")"
        }

    companion object {
        fun wrap(collection: CollectionSource, condition: Condition): SelenideElement {
            return Proxy.newProxyInstance(
                 null, arrayOf<kotlin.reflect.KClass<*>>(SelenideElement::class),
                SelenideElementProxy(CollectionElementByCondition(collection, condition))
            ) as SelenideElement
        }
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import org.openqa.selenium.WebElement
import kotlin.jvm.JvmStatic

class CollectionElementByCondition internal constructor(
    private val collection: CollectionSource,
    private val condition: Condition
) : WebElementSource() {
    override fun driver(): Driver {
        return collection.driver()
    }

    override suspend fun getWebElement(): org.openqa.selenium.WebElement {
        val list = collection.getElements()
        for (element in list) {
            if (condition.apply(driver(), element)) {
                return element
            }
        }
        throw ElementNotFound(driver(), description(), condition)
    }

    override fun getSearchCriteria(): String {
        return "${collection.description()}.findBy($condition)"
    }

    companion object {
        @JvmStatic
        fun wrap(collection: CollectionSource, condition: Condition): SelenideElement {
            return SelenideElement(
                SelenideElementProxy(CollectionElementByCondition(collection, condition))
            )
        }
    }
}

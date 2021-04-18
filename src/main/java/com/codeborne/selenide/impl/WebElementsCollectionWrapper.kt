package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue

class WebElementsCollectionWrapper(private val driver: Driver, elements: Collection<WebElement>?) : CollectionSource {
    @get:CheckReturnValue
    override val elements: List<WebElement>
    private var alias = Alias.NONE
    @CheckReturnValue
    override fun getElement(index: Int): WebElement {
        return elements[index]
    }

    @CheckReturnValue
    override fun description(): String {
        return alias.getOrElse { "$$(" + elements.size + " elements)" }
    }

    @CheckReturnValue
    override fun driver(): Driver {
        return driver
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }

    init {
        this.elements = ArrayList(elements)
    }
}

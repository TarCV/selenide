package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class CollectionSnapshot(private val originalCollection: CollectionSource) : CollectionSource {
    private val elementsSnapshot: List<WebElement>
    private var alias = Alias.NONE
    @get:CheckReturnValue
    override val elements: List<WebElement>
        get() {
            return elementsSnapshot
        }

    @CheckReturnValue
    override fun getElement(index: Int): WebElement {
        return elementsSnapshot[index]
    }

    @CheckReturnValue
    override fun description(): String {
        return alias.getOrElse {
            String.format(
              "%s.snapshot(%d elements)",
              originalCollection.description(),
              elementsSnapshot.size
            )
        }
    }

    @CheckReturnValue
    override fun driver(): Driver {
        return originalCollection.driver()
    }

    override fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }

    init {
        elementsSnapshot = ArrayList(originalCollection.elements)
    }
}

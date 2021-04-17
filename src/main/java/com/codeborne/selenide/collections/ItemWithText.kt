package com.codeborne.selenide.collections

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.ElementsCollection.Companion.texts
import com.codeborne.selenide.ex.ElementWithTextNotFound
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ItemWithText(private val expectedText: String) : CollectionCondition() {
    @CheckReturnValue
    override fun test(elements: List<WebElement>): Boolean {
        return texts(elements)
            .contains(expectedText)
    }

    override fun fail(
        collection: CollectionSource,
        elements: List<WebElement>?,
        lastError: Exception?,
        timeoutMs: Long
    ) {
        throw ElementWithTextNotFound(
            collection, texts(elements), listOf(expectedText), explanation, timeoutMs, lastError
        )
    }

    @CheckReturnValue
    override fun applyNull(): Boolean {
        return false
    }

    @CheckReturnValue
    override fun toString(): String {
        return "Text $expectedText"
    }
}

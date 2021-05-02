package com.codeborne.selenide.collections

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.ElementsCollection.Companion.texts
import com.codeborne.selenide.ex.ElementWithTextNotFound.Companion.ElementWithTextNotFound
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement

class ItemWithText(private val expectedText: String) : CollectionCondition() {
    override fun test(elements: List<org.openqa.selenium.WebElement>): Boolean {
        return texts(elements)
            .contains(expectedText)
    }

    override suspend fun fail(
        collection: CollectionSource,
        elements: List<org.openqa.selenium.WebElement>?,
        lastError: Exception?,
        timeoutMs: Long
    ) {
        throw ElementWithTextNotFound(
            collection, texts(elements), listOf(expectedText), explanation, timeoutMs, lastError
        )
    }
    override fun applyNull(): Boolean {
        return false
    }
    override fun toString(): String {
        return "Text $expectedText"
    }
}

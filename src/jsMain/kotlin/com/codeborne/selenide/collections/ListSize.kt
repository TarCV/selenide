package com.codeborne.selenide.collections

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.ex.ListSizeMismatch
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement

class ListSize(protected val expectedSize: Int) : CollectionCondition() {
    override operator fun invoke(elements: List<WebElement>): Boolean {
        return apply(elements.size)
    }

    override suspend fun fail(
        collection: CollectionSource,
        elements: List<WebElement>?,
        lastError: Exception?,
        timeoutMs: Long
    ) {
        throw ListSizeMismatch(
            collection.driver(),
            "=",
            expectedSize,
            explanation,
            collection,
            elements,
            lastError,
            timeoutMs
        )
    }
    override fun applyNull(): Boolean {
        return apply(0)
    }
    override fun toString(): String {
        return "size(${expectedSize})"
    }

    private fun apply(size: Int): Boolean {
        return size == expectedSize
    }
}
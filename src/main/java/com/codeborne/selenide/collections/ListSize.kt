package com.codeborne.selenide.collections

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.ex.ListSizeMismatch
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ListSize(protected val expectedSize: Int) : CollectionCondition() {
    @CheckReturnValue
    override fun test(elements: List<WebElement>): Boolean {
        return apply(elements.size)
    }

    override fun fail(
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

    @CheckReturnValue
    override fun applyNull(): Boolean {
        return apply(0)
    }

    @CheckReturnValue
    override fun toString(): String {
        return String.format("size(%s)", expectedSize)
    }

    private fun apply(size: Int): Boolean {
        return size == expectedSize
    }
}

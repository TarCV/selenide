package com.codeborne.selenide.collections

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.ex.ElementNotFound.Companion.ElementNotFound
import com.codeborne.selenide.ex.MatcherError.Companion.MatcherError
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement

abstract class PredicateCollectionCondition protected constructor(
    protected val matcher: String,
    protected val description: String,
    protected val predicate: (org.openqa.selenium.WebElement) -> Boolean
) : CollectionCondition() {
    override suspend fun fail(
        collection: CollectionSource,
        elements: List<org.openqa.selenium.WebElement>?,
        lastError: Exception?,
        timeoutMs: Long
    ) {
        if (elements == null || elements.isEmpty()) {
            val elementNotFound = ElementNotFound(collection, toString(), lastError)
            elementNotFound.timeoutMs = timeoutMs
            throw elementNotFound
        } else {
            throw MatcherError(matcher, description, explanation, collection, elements, lastError, timeoutMs)
        }
    }

    override fun applyNull(): Boolean {
        return false
    }

    override fun toString(): String {
        return "${matcher} match [{description}] predicate"
    }
}

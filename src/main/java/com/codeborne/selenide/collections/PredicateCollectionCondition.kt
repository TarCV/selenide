package com.codeborne.selenide.collections

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.MatcherError
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement
import java.util.function.Predicate
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
abstract class PredicateCollectionCondition protected constructor(
    protected val matcher: String,
    protected val description: String,
    protected val predicate: Predicate<WebElement>
) : CollectionCondition() {
    override fun fail(
        collection: CollectionSource,
        elements: List<WebElement>?,
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
        return String.format("%s match [%s] predicate", matcher, description)
    }
}

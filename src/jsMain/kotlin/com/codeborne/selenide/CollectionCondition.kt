package com.codeborne.selenide

import com.codeborne.selenide.collections.AllMatch
import com.codeborne.selenide.collections.AnyMatch
import com.codeborne.selenide.collections.ContainExactTextsCaseSensitive
import com.codeborne.selenide.collections.ExactTexts
import com.codeborne.selenide.collections.ExactTextsCaseSensitiveInAnyOrder
import com.codeborne.selenide.collections.ItemWithText
import com.codeborne.selenide.collections.ListSize
import com.codeborne.selenide.collections.NoneMatch
import com.codeborne.selenide.collections.SizeGreaterThan
import com.codeborne.selenide.collections.SizeGreaterThanOrEqual
import com.codeborne.selenide.collections.SizeLessThan
import com.codeborne.selenide.collections.SizeLessThanOrEqual
import com.codeborne.selenide.collections.SizeNotEqual
import com.codeborne.selenide.collections.Texts
import com.codeborne.selenide.collections.TextsInAnyOrder
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement
import support.Predicate

abstract class CollectionCondition: Predicate<List<WebElement>> {
    protected var explanation: String? = null
    abstract suspend fun fail(
        collection: CollectionSource,
        elements: List<WebElement>?,
        lastError: Exception?,
        timeoutMs: Long
    )

    /**
     * Wraps CollectionCondition without any changes except toString() method
     * where explanation string (because) are being appended
     */
    private class ExplainedCollectionCondition constructor(
        private val delegate: CollectionCondition,
        private val message: String
    ) : CollectionCondition() {
        override fun toString(): String {
            return "$delegate (because $message)"
        }

        override suspend fun fail(
          collection: CollectionSource,
          elements: List<WebElement>?,
          lastError: Exception?,
          timeoutMs: Long
        ) {
            delegate.fail(collection, elements, lastError, timeoutMs)
        }

        override fun applyNull(): Boolean {
            return delegate.applyNull()
        }

        override operator fun invoke(input: List<WebElement>): Boolean {
            return delegate.invoke(input)
        }
    }

    /**
     * Should be used for explaining the reason of condition
     */
    fun because(explanation: String): CollectionCondition {
        this.explanation = explanation
        return ExplainedCollectionCondition(this, explanation)
    }

    abstract fun applyNull(): Boolean

    companion object {
        var empty = size(0)

        /**
         * Checks that collection has the given size
         */
        fun size(expectedSize: Int): CollectionCondition {
            return ListSize(expectedSize)
        }

        fun sizeGreaterThan(expectedSize: Int): CollectionCondition {
            return SizeGreaterThan(expectedSize)
        }

        fun sizeGreaterThanOrEqual(expectedSize: Int): CollectionCondition {
            return SizeGreaterThanOrEqual(expectedSize)
        }

        fun sizeLessThan(expectedSize: Int): CollectionCondition {
            return SizeLessThan(expectedSize)
        }

        fun sizeLessThanOrEqual(size: Int): CollectionCondition {
            return SizeLessThanOrEqual(size)
        }

        fun sizeNotEqual(expectedSize: Int): CollectionCondition {
            return SizeNotEqual(expectedSize)
        }

        /**
         * Checks that given collection has given texts (each collection element CONTAINS corresponding text)
         *
         *
         * NB! Ignores multiple whitespaces between words
         */
        fun texts(vararg expectedTexts: String): CollectionCondition {
            return Texts(*expectedTexts)
        }

        /**
         * Checks that given collection has given texts (each collection element CONTAINS corresponding text)
         *
         *
         * NB! Ignores multiple whitespaces between words
         */
        fun texts(expectedTexts: List<String>): CollectionCondition {
            return Texts(expectedTexts)
        }

        /**
         * Checks that given collection has given texts in any order (each collection element CONTAINS corresponding text)
         *
         *
         * NB! Ignores multiple whitespaces between words
         */
        fun textsInAnyOrder(vararg expectedTexts: String): CollectionCondition {
            return TextsInAnyOrder(*expectedTexts)
        }

        /**
         * Checks that given collection has given texts in any order (each collection element CONTAINS corresponding text)
         *
         *
         * NB! Ignores multiple whitespaces between words
         */
        fun textsInAnyOrder(expectedTexts: List<String>): CollectionCondition {
            return TextsInAnyOrder(expectedTexts)
        }

        /**
         * Checks that given collection has given texts (each collection element EQUALS TO corresponding text)
         *
         *
         * NB! Ignores multiple whitespaces between words
         */
        fun exactTexts(vararg expectedTexts: String): CollectionCondition {
            return ExactTexts(*expectedTexts)
        }

        /**
         * Checks that given collection has given texts (each collection element EQUALS TO corresponding text)
         *
         *
         * NB! Ignores multiple whitespaces between words
         */
        fun exactTexts(expectedTexts: List<String>): CollectionCondition {
            return ExactTexts(expectedTexts)
        }

        /**
         * Checks if ANY elements of this collection match the provided predicate
         *
         * @param description The description of the given predicate
         * @param predicate   the [java.util.function.Predicate] to match
         */
        fun anyMatch(description: String, predicate: (WebElement) -> Boolean): CollectionCondition {
            return AnyMatch(description, predicate)
        }

        /**
         * Checks if ALL elements of this collection match the provided predicate
         *
         * @param description The description of the given predicate
         * @param predicate   the [java.util.function.Predicate] to match
         */
        fun allMatch(description: String, predicate: (WebElement) -> Boolean): CollectionCondition {
            return AllMatch(description, predicate)
        }

        /**
         * Checks if NONE elements of this collection match the provided predicate
         *
         * @param description The description of the given predicate
         * @param predicate   the [java.util.function.Predicate] to match
         */
        fun noneMatch(description: String, predicate: (WebElement) -> Boolean): CollectionCondition {
            return NoneMatch(description, predicate)
        }

        /**
         * Checks if given collection has an element with given text.
         * The condition is satisfied if one or more elements in this collection have exactly the given text.
         *
         * @param expectedText The expected text in the collection
         */
        fun itemWithText(expectedText: String): CollectionCondition {
            return ItemWithText(expectedText)
        }

        /**
         * Check that the given collection contains all elements with given texts.
         *
         *  NB! This condition is case-sensitive and checks for exact matches!
         * Examples:
         * `
         * // collection 1: [Tom, Dick, Harry]
         * $$("li.odd").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // success
         * // collection 2: [Tom, John, Dick, Harry]
         * $$("li.even").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // success
         * // collection 3: [John, Dick, Tom, Paul]
         * $$("li.first").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // fail ("Harry" is missing)
         * // collection 4: [Tom, Dick, hArRy]
         * $$("li.last").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // fail ("Harry" is missing)
        ` *
         *
         * @param expectedTexts the expected texts that the collection should contain
         */
        fun containExactTextsCaseSensitive(vararg expectedTexts: String): CollectionCondition {
            return ContainExactTextsCaseSensitive(*expectedTexts)
        }

        /**
         * Check that the given collection contains all elements with given texts.
         *
         *  NB! This condition is case-sensitive and checks for exact matches!
         * Examples:
         * `
         * // collection 1: [Tom, Dick, Harry]
         * $$("li.odd").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // success
         * // collection 2: [Tom, John, Dick, Harry]
         * $$("li.even").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // success
         * // collection 3: [John, Dick, Tom, Paul]
         * $$("li.first").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // fail ("Harry" is missing)
         * // collection 4: [Tom, Dick, hArRy]
         * $$("li.last").should(containExactTextsCaseSensitive("Tom", "Dick", "Harry")); // fail ("Harry" is missing)
        ` *
         *
         * @param expectedTexts the expected texts that the collection should contain
         */
        fun containExactTextsCaseSensitive(expectedTexts: List<String>): CollectionCondition {
            return ContainExactTextsCaseSensitive(expectedTexts)
        }

        /**
         * Checks that given collection has given texts in any order (each collection element EQUALS TO corresponding text)
         *
         *
         * NB! Case sensitive
         *
         * @param expectedTexts Expected texts in any order in the collection
         */
        fun exactTextsCaseSensitiveInAnyOrder(expectedTexts: List<String>): CollectionCondition {
            return ExactTextsCaseSensitiveInAnyOrder(expectedTexts)
        }

        /**
         * Checks that given collection has given texts in any order (each collection element EQUALS TO corresponding text)
         *
         *
         * NB! Case sensitive
         *
         * @param expectedTexts Expected texts in any order in the collection
         */
        fun exactTextsCaseSensitiveInAnyOrder(vararg expectedTexts: String): CollectionCondition {
            return ExactTextsCaseSensitiveInAnyOrder(*expectedTexts)
        }
    }
}

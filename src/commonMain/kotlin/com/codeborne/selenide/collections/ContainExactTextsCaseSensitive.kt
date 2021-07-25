package com.codeborne.selenide.collections

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.ElementsCollection.Companion.texts
import com.codeborne.selenide.ex.DoesNotContainTextsError
import com.codeborne.selenide.ex.ElementNotFound.Companion.ElementNotFound
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement

class ContainExactTextsCaseSensitive(expectedTexts: List<String>) : CollectionCondition() {
    private val expectedTexts: List<String>

    constructor(vararg expectedTexts: String) : this(listOf<String>(*expectedTexts))

    override suspend fun test(input: List<WebElement>): Boolean {
        return if (input.size < expectedTexts.size) {
            false
        } else texts(input)
            .containsAll(expectedTexts)
    }

    override suspend fun fail(
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
            val actualTexts: List<String?> = texts(elements)
            val difference: MutableList<String?> = ArrayList(expectedTexts)
            difference.removeAll(actualTexts)
            throw DoesNotContainTextsError(
                collection,
                actualTexts, expectedTexts, difference, explanation,
                timeoutMs, lastError
            )
        }
    }

    override fun applyNull(): Boolean {
        return false
    }

    override fun toString(): String {
        return "Contains exact texts case-sensitive $expectedTexts"
    }

    init {
        require(expectedTexts.isNotEmpty()) { "No expected texts given" }
        this.expectedTexts = (expectedTexts)
    }
}

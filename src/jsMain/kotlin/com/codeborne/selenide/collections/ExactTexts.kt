package com.codeborne.selenide.collections

import com.codeborne.selenide.ElementsCollection.Companion.texts
import com.codeborne.selenide.CollectionCondition
import org.openqa.selenium.WebElement
import com.codeborne.selenide.impl.Html
import com.codeborne.selenide.impl.CollectionSource
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.TextsSizeMismatch
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.ex.TextsMismatch

open class ExactTexts(expectedTexts: List<String>) : CollectionCondition() {
    val expectedTexts: List<String>

    constructor(vararg expectedTexts: String) : this(listOf<String>(*expectedTexts)) {}
    override operator fun invoke(elements: List<WebElement>): Boolean {
        if (elements.size != expectedTexts.size) {
            return false
        }
        for (i in expectedTexts.indices) {
            val element = elements[i]
            val expectedText = expectedTexts[i]
            if (!Html.text.equals(element.text, expectedText)) {
                return false
            }
        }
        return true
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
        } else if (elements.size != expectedTexts.size) {
            throw TextsSizeMismatch(collection, texts(elements), expectedTexts, explanation, timeoutMs)
        } else {
            throw TextsMismatch(collection, texts(elements), expectedTexts, explanation, timeoutMs)
        }
    }

    override fun applyNull(): Boolean {
        return false
    }

    override fun toString(): String {
        return "Exact texts $expectedTexts"
    }

    init {
        require(expectedTexts.isNotEmpty()) { "No expected texts given" }
        this.expectedTexts = (expectedTexts)
    }
}

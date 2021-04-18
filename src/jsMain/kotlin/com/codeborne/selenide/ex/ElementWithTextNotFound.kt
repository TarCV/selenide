package com.codeborne.selenide.ex

import com.codeborne.selenide.impl.CollectionSource

class ElementWithTextNotFound(
    collection: CollectionSource, actualTexts: List<String>,
    expectedTexts: List<String>, explanation: String?,
    timeoutMs: Long, lastError: Throwable?
) : UIAssertionError(
    collection.driver(),
    "Element with text not found" +
            "\n" + "Actual: " + actualTexts +
            "\n" + "Expected: " + expectedTexts +
            (if (explanation == null) "" else "\n" + "Because: " + explanation) +
            "\n" + "Collection: " + collection.description(),
    lastError
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

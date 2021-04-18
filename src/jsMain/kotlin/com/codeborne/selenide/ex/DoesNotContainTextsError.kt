package com.codeborne.selenide.ex

import com.codeborne.selenide.impl.CollectionSource

class DoesNotContainTextsError(
    collection: CollectionSource,
    actualTexts: List<String?>, expectedTexts: List<String?>, difference: List<String?>,
    explanation: String?, timeoutMs: Long, lastError: Throwable?
) : UIAssertionError(
    collection.driver(),
    "The collection with text elements: " + actualTexts +
            "\n" + "should contain all of the following text elements: " + expectedTexts +
            (if (explanation == null) "" else "\n" + "Because: " + explanation) +
            "\n" + "but could not find these elements: " + difference +
            "\n" + "Collection: " + collection.description(),
    lastError
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

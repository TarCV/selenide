package com.codeborne.selenide.ex

import com.codeborne.selenide.impl.CollectionSource
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class DoesNotContainTextsError(
    collection: CollectionSource,
    actualTexts: List<String?>, expectedTexts: List<String?>, difference: List<String?>,
    explanation: String?, timeoutMs: Long, lastError: Throwable?
) : UIAssertionError(
    collection.driver(),
    "The collection with text elements: " + actualTexts +
            System.lineSeparator() + "should contain all of the following text elements: " + expectedTexts +
            (if (explanation == null) "" else System.lineSeparator() + "Because: " + explanation) +
            System.lineSeparator() + "but could not find these elements: " + difference +
            System.lineSeparator() + "Collection: " + collection.description(),
    lastError
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

package com.codeborne.selenide.ex

import com.codeborne.selenide.impl.CollectionSource
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ElementWithTextNotFound(
    collection: CollectionSource, actualTexts: List<String>,
    expectedTexts: List<String>, explanation: String?,
    timeoutMs: Long, lastError: Throwable?
) : UIAssertionError(
    collection.driver(),
    "Element with text not found" +
            System.lineSeparator() + "Actual: " + actualTexts +
            System.lineSeparator() + "Expected: " + expectedTexts +
            (if (explanation == null) "" else System.lineSeparator() + "Because: " + explanation) +
            System.lineSeparator() + "Collection: " + collection.description(),
    lastError
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

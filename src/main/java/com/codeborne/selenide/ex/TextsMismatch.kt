package com.codeborne.selenide.ex

import com.codeborne.selenide.impl.CollectionSource
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class TextsMismatch(
    collection: CollectionSource, actualTexts: List<String?>,
    expectedTexts: List<String?>, explanation: String?, timeoutMs: Long
) : UIAssertionError(
    collection.driver(),
    "Texts mismatch" +
            System.lineSeparator() + "Actual: " + actualTexts +
            System.lineSeparator() + "Expected: " + expectedTexts +
            (if (explanation == null) "" else System.lineSeparator() + "Because: " + explanation) +
            System.lineSeparator() + "Collection: " + collection.description()
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

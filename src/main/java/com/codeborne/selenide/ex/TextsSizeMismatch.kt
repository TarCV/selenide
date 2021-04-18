package com.codeborne.selenide.ex

import com.codeborne.selenide.impl.CollectionSource
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class TextsSizeMismatch(
    collection: CollectionSource, actualTexts: List<String?>,
    expectedTexts: List<String?>, explanation: String?, timeoutMs: Long
) : UIAssertionError(
    collection.driver(),
    "Texts size mismatch" +
            System.lineSeparator() + "Actual: " + actualTexts + ", List size: " + actualTexts.size +
            System.lineSeparator() + "Expected: " + expectedTexts + ", List size: " + expectedTexts.size +
            (if (explanation == null) "" else System.lineSeparator() + "Because: " + explanation) +
            System.lineSeparator() + "Collection: " + collection.description()
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

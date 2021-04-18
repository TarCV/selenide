package com.codeborne.selenide.ex

import com.codeborne.selenide.impl.CollectionSource

class TextsSizeMismatch(
    collection: CollectionSource, actualTexts: List<String?>,
    expectedTexts: List<String?>, explanation: String?, timeoutMs: Long
) : UIAssertionError(
    collection.driver(),
    "Texts size mismatch" +
            "\n" + "Actual: " + actualTexts + ", List size: " + actualTexts.size +
            "\n" + "Expected: " + expectedTexts + ", List size: " + expectedTexts.size +
            (if (explanation == null) "" else "\n" + "Because: " + explanation) +
            "\n" + "Collection: " + collection.description()
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

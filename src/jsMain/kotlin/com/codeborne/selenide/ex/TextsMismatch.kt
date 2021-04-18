package com.codeborne.selenide.ex

import com.codeborne.selenide.impl.CollectionSource

class TextsMismatch(
    collection: CollectionSource, actualTexts: List<String?>,
    expectedTexts: List<String?>, explanation: String?, timeoutMs: Long
) : UIAssertionError(
    collection.driver(),
    "Texts mismatch" +
            "\n" + "Actual: " + actualTexts +
            "\n" + "Expected: " + expectedTexts +
            (if (explanation == null) "" else "\n" + "Because: " + explanation) +
            "\n" + "Collection: " + collection.description()
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

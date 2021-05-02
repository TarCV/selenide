package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.CollectionSource

class TextsMismatch internal constructor(
    driver: Driver,
    message: String,
    timeoutMs: Long
) : UIAssertionError(
    driver,
    message
) {
    init {
        super.timeoutMs = timeoutMs
    }

    companion object {
        suspend fun TextsMismatch(
            collection: CollectionSource, actualTexts: List<String?>,
            expectedTexts: List<String?>, explanation: String?, timeoutMs: Long
        ) = TextsMismatch(
            collection.driver(),
            "Texts mismatch" +
                "\n" + "Actual: " + actualTexts +
                "\n" + "Expected: " + expectedTexts +
                (if (explanation == null) "" else "\nBecause: $explanation") +
                "\n" + "Collection: " + collection.description(),
            timeoutMs
        )
    }
}

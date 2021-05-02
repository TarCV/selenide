package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.CollectionSource

class TextsSizeMismatch internal constructor(
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
        suspend fun TextsSizeMismatch(
            collection: CollectionSource, actualTexts: List<String?>,
            expectedTexts: List<String?>, explanation: String?, timeoutMs: Long
        ) = TextsSizeMismatch(
            collection.driver(),
            "Texts size mismatch" +
                "\n" + "Actual: " + actualTexts + ", List size: " + actualTexts.size +
                "\n" + "Expected: " + expectedTexts + ", List size: " + expectedTexts.size +
                (if (explanation == null) "" else "\nBecause: $explanation") +
                "\n" + "Collection: " + collection.description(),
            timeoutMs
        )
    }
}

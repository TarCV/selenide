package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.CollectionSource

suspend fun DoesNotContainTextsError(
    collection: CollectionSource,
    actualTexts: List<String?>, expectedTexts: List<String?>, difference: List<String?>,
    explanation: String?, timeoutMs: Long, lastError: Throwable?
) = DoesNotContainTextsError(
    collection.driver(),
    "The collection with text elements: " + actualTexts +
        "\n" + "should contain all of the following text elements: " + expectedTexts +
        (if (explanation == null) "" else "\nBecause: $explanation") +
        "\n" + "but could not find these elements: " + difference +
        "\n" + "Collection: " + collection.description(),
    lastError
)

class DoesNotContainTextsError internal constructor(
    driver: Driver,
    message: String,
    lastError: Throwable?
) : UIAssertionError(
    driver,
    message,
    lastError
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

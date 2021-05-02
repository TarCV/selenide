package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.CollectionSource

suspend fun ElementWithTextNotFound(
    collection: CollectionSource, actualTexts: List<String>,
    expectedTexts: List<String>, explanation: String?,
    timeoutMs: Long, lastError: Throwable?
) = ElementWithTextNotFound(
    collection.driver(),
    "Element with text not found" +
        "\n" + "Actual: " + actualTexts +
        "\n" + "Expected: " + expectedTexts +
        (if (explanation == null) "" else "\nBecause: $explanation") +
        "\n" + "Collection: " + collection.description(),
    lastError
)
class ElementWithTextNotFound internal constructor(
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

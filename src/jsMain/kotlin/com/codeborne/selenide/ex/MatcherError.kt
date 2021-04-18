package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsCollection.Companion.elementsToString
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement

suspend fun MatcherError(
    matcher: String,
    predicateDescription: String,
    explanation: String?,
    collection: CollectionSource,
    actualElements: List<WebElement>?,
    lastError: Exception?,
    timeoutMs: Long
): MatcherError {
    val message = "Collection matcher error" +
        "\n" + "Expected: " + matcher + " of elements to match [" + predicateDescription + "] predicate" +
        (if (explanation == null) "" else "\n" + "Because: " + explanation) +
        "\n" + "Collection: " + collection.description() +
        "\n" + "Elements: " + elementsToString(collection.driver(), actualElements)
    return MatcherError(collection.driver(), message, lastError, timeoutMs)
}
class MatcherError internal constructor(
    driver: Driver,
    message: String,
    lastError: Exception?,
    timeoutMs: Long
) : UIAssertionError(
    driver, message, lastError
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

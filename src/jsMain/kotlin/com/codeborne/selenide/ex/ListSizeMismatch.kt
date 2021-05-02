package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsCollection.Companion.elementsToString
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement

suspend fun ListSizeMismatch(
    driver: Driver, operator: String, expectedSize: Int,
    explanation: String?,
    collection: CollectionSource,
    actualElements: List<WebElement>?,
    lastError: Exception?,
    timeoutMs: Long
): ListSizeMismatch {
    val message = "List size mismatch: expected: " + operator + ' ' + expectedSize +
        (if (explanation == null) "" else " (because $explanation)") +
        ", actual: " + (actualElements?.size ?: 0) +
        ", collection: " + collection.description() +
        "\n" + "Elements: " + elementsToString(collection.driver(), actualElements)
    return ListSizeMismatch(driver, message, lastError)
}

class ListSizeMismatch internal constructor(
    driver: Driver, message: String, lastError: Exception?
) : UIAssertionError(
  driver, message, lastError
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

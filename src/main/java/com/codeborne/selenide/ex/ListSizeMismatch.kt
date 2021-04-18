package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsCollection.Companion.elementsToString
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ListSizeMismatch(
    driver: Driver, operator: String, expectedSize: Int,
    explanation: String?,
    collection: CollectionSource,
    actualElements: List<WebElement>?,
    lastError: Exception?,
    timeoutMs: Long
) : UIAssertionError(
  driver,
    "List size mismatch: expected: " + operator + ' ' + expectedSize +
            (if (explanation == null) "" else " (because $explanation)") +
            ", actual: " + (actualElements?.size ?: 0) +
            ", collection: " + collection.description() +
            System.lineSeparator() + "Elements: " + elementsToString(collection.driver(), actualElements), lastError
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

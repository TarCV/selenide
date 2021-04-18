package com.codeborne.selenide.ex

import com.codeborne.selenide.ElementsCollection.Companion.elementsToString
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class MatcherError(
    matcher: String,
    predicateDescription: String,
    explanation: String?,
    collection: CollectionSource,
    actualElements: List<WebElement>?,
    lastError: Exception?,
    timeoutMs: Long
) : UIAssertionError(
    collection.driver(),
    "Collection matcher error" +
            System.lineSeparator() + "Expected: " + matcher + " of elements to match [" + predicateDescription + "] predicate" +
            (if (explanation == null) "" else System.lineSeparator() + "Because: " + explanation) +
            System.lineSeparator() + "Collection: " + collection.description() +
            System.lineSeparator() + "Elements: " + elementsToString(collection.driver(), actualElements), lastError
) {
    init {
        super.timeoutMs = timeoutMs
    }
}

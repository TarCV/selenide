package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.By

class ElementNotFound : UIAssertionError {
    constructor(driver: Driver, searchCriteria: By, expectedCondition: Condition?) : this(
        driver,
        searchCriteria.toString(),
        expectedCondition,
        null
    ) {
    }

    constructor(driver: Driver, searchCriteria: String?, expectedCondition: Condition?) : super(
      driver,
            "Element not found {$searchCriteria}" +
                    "\nExpected: $expectedCondition"
    ) {
    }

    constructor(driver: Driver, searchCriteria: String?, expectedCondition: Condition?, lastError: Throwable?) : super(
      driver,
            "Element not found {$searchCriteria}" +
                    "\nExpected: $expectedCondition"
        , lastError
    ) {
    }

    constructor(collection: CollectionSource, expectedTexts: List<String?>?, lastError: Throwable?) : super(
        collection.driver(),
            "Element not found {${collection.description()}}" +
                    "\nExpected: $expectedTexts"
        lastError
    ) {
    }

    constructor(collection: CollectionSource, description: String?, lastError: Throwable?) : super(
        collection.driver(),
            "Element not found {${collection.description()}}" +
                    "\nExpected: $description"
        , lastError
    ) {
    }
}

package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.CollectionSource

class ElementNotFound : UIAssertionError {
    constructor(driver: Driver, searchCriteria: org.openqa.selenium.By, expectedCondition: Condition?) : this(
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

    internal constructor(
        driver: Driver,
        message: String,
        lastError: Throwable?
    ) : super(driver, message, lastError)

    companion object {
        suspend fun ElementNotFound(collection: CollectionSource, expectedTexts: List<String?>?, lastError: Throwable?): ElementNotFound {
            return ElementNotFound(
                collection.driver(),
                "Element not found {${collection.description()}}" +
                    "\nExpected: $expectedTexts",
                lastError
            )
        }

        suspend fun ElementNotFound(collection: CollectionSource, description: String?, lastError: Throwable?): ElementNotFound {
            return ElementNotFound(
                collection.driver(),
                "Element not found {${collection.description()}}" +
                    "\nExpected: $description"
                , lastError
            )
        }
    }
}


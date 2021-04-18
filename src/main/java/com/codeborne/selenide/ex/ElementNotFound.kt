package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.By
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ElementNotFound : UIAssertionError {
    constructor(driver: Driver, searchCriteria: By, expectedCondition: Condition?) : this(
        driver,
        searchCriteria.toString(),
        expectedCondition,
        null
    ) {
    }

    constructor(driver: Driver, searchCriteria: String?, expectedCondition: Condition?) : super(
      driver, String.format(
            "Element not found {%s}" +
                    "%nExpected: %s", searchCriteria, expectedCondition
        )
    ) {
    }

    constructor(driver: Driver, searchCriteria: String?, expectedCondition: Condition?, lastError: Throwable?) : super(
      driver, String.format(
            "Element not found {%s}" +
                    "%nExpected: %s", searchCriteria, expectedCondition
        ), lastError
    ) {
    }

    constructor(collection: CollectionSource, expectedTexts: List<String?>?, lastError: Throwable?) : super(
        collection.driver(), String.format(
            "Element not found {%s}" +
                    "%nExpected: %s", collection.description(), expectedTexts
        ), lastError
    ) {
    }

    constructor(collection: CollectionSource, description: String?, lastError: Throwable?) : super(
        collection.driver(), String.format(
            "Element not found {%s}" +
                    "%nExpected: %s", collection.description(), description
        ), lastError
    ) {
    }
}

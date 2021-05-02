package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.Plugins
import org.openqa.selenium.WebElement

class ElementShould internal constructor(
    driver: Driver, message: String, lastError: Throwable?
): UIAssertionError(driver, message, lastError) {
    companion object {
        internal val describe = Plugins.injectA(
            ElementDescriber::class
        )

        suspend fun ElementShould(
            driver: Driver, searchCriteria: String?, prefix: String?, expectedCondition: Condition,
            element: org.openqa.selenium.WebElement?, lastError: Throwable?
        ): ElementShould {
            val message = "Element should $prefix$expectedCondition {$searchCriteria}\nElement: '${ElementShould.describe.fully(driver, element)}'${ErrorMessages.actualValue(expectedCondition, driver, element)}"
            return ElementShould(driver, message, lastError)
        }
    }
}

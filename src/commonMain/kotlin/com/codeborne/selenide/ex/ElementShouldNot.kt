package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Plugins
import org.openqa.selenium.WebElement

class ElementShouldNot internal constructor(driver: Driver, message: String?, lastError: Throwable?) : UIAssertionError(driver, message, lastError) {
    companion object {
        private val describe = Plugins.elementDescriber

        suspend fun ElementShouldNot(
            driver: Driver, searchCriteria: String?, prefix: String?, expectedCondition: Condition,
            element: org.openqa.selenium.WebElement?, lastError: Throwable?
        ): ElementShouldNot {
            val message = "Element should not $expectedCondition {$searchCriteria}\nElement: '${ElementShould.describe.fully(driver, element)}'${ErrorMessages.actualValue(expectedCondition, driver, element)}"
            return ElementShouldNot(driver, message, lastError)
        }
    }
}

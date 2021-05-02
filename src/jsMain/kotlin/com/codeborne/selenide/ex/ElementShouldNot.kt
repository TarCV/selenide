package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.ex.ElementShould.Companion.describe
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.Plugins.inject
import org.openqa.selenium.WebElement

suspend fun ElementShouldNot(
    driver: Driver, searchCriteria: String?, prefix: String?, expectedCondition: Condition,
    element: WebElement?, lastError: Throwable?
): ElementShouldNot {
    val message = "Element should not $expectedCondition {$searchCriteria}\nElement: '${describe.fully(driver, element)}'${ErrorMessages.actualValue(expectedCondition, driver, element)}"
    return ElementShouldNot(driver, message, lastError)
}

class ElementShouldNot internal constructor(driver: Driver, message: String?, lastError: Throwable?) : UIAssertionError(driver, message, lastError) {
    companion object {
        private val describe = inject(ElementDescriber::class)
    }
}

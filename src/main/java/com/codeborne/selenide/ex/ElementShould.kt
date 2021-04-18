package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.Plugins
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ElementShould(
    driver: Driver, searchCriteria: String?, prefix: String?, expectedCondition: Condition,
    element: WebElement?, lastError: Throwable?
) : UIAssertionError(
    driver, String.format(
        "Element should %s%s {%s}%nElement: '%s'%s",
        prefix, expectedCondition, searchCriteria,
        describe.fully(driver, element),
        ErrorMessages.actualValue(expectedCondition, driver, element)
    ), lastError
) {
    companion object {
        private val describe = Plugins.inject(
            ElementDescriber::class.java
        )
    }
}

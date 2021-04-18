package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.Plugins.inject
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ElementShouldNot(
    driver: Driver, searchCriteria: String?, prefix: String?, expectedCondition: Condition,
    element: WebElement?, lastError: Throwable?
) : UIAssertionError(
    driver, String.format(
        "Element should not %s%s {%s}%sElement: '%s'%s",
        prefix, expectedCondition, searchCriteria, System.lineSeparator(),
        describe.fully(driver, element),
        ErrorMessages.actualValue(expectedCondition, driver, element)
    ), lastError
) {
    companion object {
        private val describe = inject(ElementDescriber::class.java)
    }
}

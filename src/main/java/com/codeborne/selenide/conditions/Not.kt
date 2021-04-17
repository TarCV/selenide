package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Not(private val condition: Condition, absentElementMatchesCondition: Boolean) :
    Condition("not " + condition.name, absentElementMatchesCondition) {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return !condition.apply(driver, element)
    }

    override fun actualValue(driver: Driver, element: WebElement): String? {
        return condition.actualValue(driver, element)
    }

    override fun toString(): String {
        return "not $condition"
    }
}

package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.conditions.ConditionHelpers.negateMissingElementTolerance
import org.openqa.selenium.WebElement
import java.util.stream.Collectors
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Or(name: String, private val conditions: List<Condition>) : Condition(name) {
    override fun negate(): Condition {
        return Not(this, negateMissingElementTolerance(conditions))
    }

    override fun apply(driver: Driver, element: WebElement): Boolean {
        for (c in conditions) {
            if (c.apply(driver, element)) {
                return true
            }
        }
        return false
    }

    override fun actualValue(driver: Driver, element: WebElement): String? {
        return conditions.stream().map { condition: Condition -> condition.actualValue(driver, element) }
            .collect(Collectors.joining(", "))
    }

    override fun toString(): String {
        val conditionsToString = conditions.stream().map { obj: Condition -> obj.toString() }
            .collect(Collectors.joining(" or "))
        return String.format("%s: %s", name, conditionsToString)
    }
}

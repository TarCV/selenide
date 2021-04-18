package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import java.util.stream.Collectors
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class And(name: String, private val conditions: List<Condition>) : Condition(
  name
) {
    private var lastFailedCondition: Condition? = null
    override fun negate(): Condition {
        return Not(this, ConditionHelpers.negateMissingElementTolerance(conditions))
    }

    @CheckReturnValue
    override fun apply(driver: Driver, element: WebElement): Boolean {
        lastFailedCondition = null
        for (c in conditions) {
            if (!c.apply(driver, element)) {
                lastFailedCondition = c
                return false
            }
        }
        return true
    }

    @CheckReturnValue
    override fun actualValue(driver: Driver, element: WebElement): String? {
        return lastFailedCondition?.actualValue(driver, element)
    }

    @CheckReturnValue
    override fun toString(): String {
        val conditionsToString = conditions.stream().map { obj: Condition -> obj.toString() }
            .collect(Collectors.joining(" and "))
        return String.format("%s: %s", name, conditionsToString)
    }
}

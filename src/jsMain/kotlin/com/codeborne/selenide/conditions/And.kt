package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class And(name: String, private val conditions: List<Condition>) : Condition(
  name
) {
    private var lastFailedCondition: Condition? = null
    override fun negate(): Condition {
        return Not(this, ConditionHelpers.negateMissingElementTolerance(conditions))
    }
    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        lastFailedCondition = null
        for (c in conditions) {
            if (!c.apply(driver, element)) {
                lastFailedCondition = c
                return false
            }
        }
        return true
    }
    override suspend fun actualValue(driver: Driver, element: WebElement): String? {
        return lastFailedCondition?.actualValue(driver, element)
    }
    override fun toString(): String {
        val conditionsToString = conditions
            .map { obj: Condition -> obj.toString() }
            .joinToString(" and ")
        return "${name}: {conditionsToString}"
    }
}

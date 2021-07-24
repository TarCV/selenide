package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.conditions.ConditionHelpers.negateMissingElementTolerance
import org.openqa.selenium.WebElement

class Or(name: String, private val conditions: List<Condition>) : Condition(name) {
    override fun negate(): Condition {
        return Not(this, negateMissingElementTolerance(conditions))
    }

    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        for (c in conditions) {
            if (c.apply(driver, element)) {
                return true
            }
        }
        return false
    }

    override suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String? {
        return conditions.map { condition: Condition -> condition.actualValue(driver, element) }
            .joinToString(", ")
    }

    override fun toString(): String {
        val conditionsToString = conditions.map { obj: Condition -> obj.toString() }
            .joinToString(" or ")
        return "${name}: ${conditionsToString}"
    }
}

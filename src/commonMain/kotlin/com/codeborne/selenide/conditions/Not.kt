package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class Not(private val condition: Condition, absentElementMatchesCondition: Boolean) :
    Condition("not " + condition.name, absentElementMatchesCondition) {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return !condition.apply(driver, element)
    }

    override suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String? {
        return condition.actualValue(driver, element)
    }

    override fun toString(): String {
        return "not $condition"
    }
}

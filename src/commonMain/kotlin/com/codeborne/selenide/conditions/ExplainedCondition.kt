package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class ExplainedCondition(private val delegate: Condition, private val message: String) : Condition(
    delegate.name, delegate.missingElementSatisfiesCondition()
) {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return delegate.apply(driver, element)
    }

    override fun negate(): Condition {
        return delegate.negate().because(message)
    }

    override suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String? {
        return delegate.actualValue(driver, element)
    }

    override fun toString(): String {
        return "$delegate (because $message)"
    }
}

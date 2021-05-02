package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition

object ConditionHelpers {
    fun <T> merge(first: T, second: T, vararg others: T): List<T> {
        val result: MutableList<T> = ArrayList(2 + others.size)
        result.add(first)
        result.add(second)
        if (others.isNotEmpty()) result.addAll(listOf(*others))
        return (result)
    }

    fun negateMissingElementTolerance(conditions: List<Condition>): Boolean {
        var result = true
        for (condition in conditions) {
            result = result and condition.negate().applyNull()
        }
        return result
    }
}

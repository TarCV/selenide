package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import java.util.Arrays
import java.util.Collections
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
object ConditionHelpers {
    @CheckReturnValue
    @SafeVarargs
    fun <T> merge(first: T, second: T, vararg others: T): List<T> {
        val result: MutableList<T> = ArrayList(2 + others.size)
        result.add(first)
        result.add(second)
        if (others.isNotEmpty()) result.addAll(listOf(*others))
        return Collections.unmodifiableList(result)
    }

    @JvmStatic
    @CheckReturnValue
    fun negateMissingElementTolerance(conditions: List<Condition>): Boolean {
        var result = true
        for (condition in conditions) {
            result = result and condition.negate().applyNull()
        }
        return result
    }
}

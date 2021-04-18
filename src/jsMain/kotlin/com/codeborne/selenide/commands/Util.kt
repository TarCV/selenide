package com.codeborne.selenide.commands

import com.codeborne.selenide.Condition
import kotlin.time.Duration

internal object Util {
    fun <T> firstOf(args: Array<out Any?>?): T {
        require(!(args == null || args.isEmpty())) { "Missing arguments" }
        return args[0] as T
    }

    @kotlin.time.ExperimentalTime
    fun argsToConditions(args: Array<out Any?>?): List<Condition> {
        if (args == null) return emptyList()
        val conditions: MutableList<Condition> = ArrayList(args.size)
        for (arg in args) {
          arg.let { when {
              it is Condition -> conditions.add(it)
              it is Array<*> && it[0] is Condition -> conditions.addAll( // TODO: why check in Java code was incomplete?
                (it as Array<Condition>).toList()
              )
              else -> require(
                it is String || it is Long || it is Duration
              ) { "Unknown parameter: $it" }
          } }
        }
        return conditions
    }
}

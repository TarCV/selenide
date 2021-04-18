package com.codeborne.selenide.impl

import java.util.Arrays
import java.util.Optional
import javax.annotation.CheckReturnValue

class Arguments(private val args: Array<out Any?>?) {
    fun <T> nth(index: Int): T {
        requireNotNull(args) { "Missing arguments" }
        require(index < args.size) { "Missing argument #" + index + " in " + Arrays.toString(args) }
        return args[index] as T
    }

    fun length(): Int {
        return args?.size ?: 0
    }

    @CheckReturnValue
    fun <T> ofType(klass: Class<T>): Optional<T> {
        if (args == null) return Optional.empty()
        for (arg in args) {
            if (arg != null && klass.isAssignableFrom(arg.javaClass)) return Optional.of(arg as T)
        }
        return Optional.empty()
    }
}

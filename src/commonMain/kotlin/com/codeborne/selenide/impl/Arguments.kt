package com.codeborne.selenide.impl


class Arguments(private val args: Array<out Any?>?) {
    fun <T> nth(index: Int): T {
        requireNotNull(args) { "Missing arguments" }
        require(index < args.size) { "Missing argument #" + index + " in " + args.toString() }
        return args[index] as T
    }

    fun length(): Int {
        return args?.size ?: 0
    }
    fun <T: Any> ofType(klass: kotlin.reflect.KClass<T>): T? {
        if (args == null) return null
        for (arg in args) {
            if (arg != null && klass.isInstance(arg::class)) return arg as T?
        }
        return null
    }
}

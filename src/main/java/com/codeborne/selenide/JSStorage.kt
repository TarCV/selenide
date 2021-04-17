package com.codeborne.selenide

import java.util.Optional
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
abstract class JSStorage(private val driver: Driver, private val storage: String) {
    @CheckReturnValue
    fun containsItem(key: String): Boolean {
        return Optional.ofNullable(getItem(key)).isPresent
    }

    @CheckReturnValue
    fun getItem(key: String): String? {
        return driver.executeJavaScript(js("return %s.getItem(arguments[0])"), key)
    }

    fun setItem(key: String, value: String) {
        driver.executeJavaScript<Any>(js("%s.setItem(arguments[0], arguments[1])"), key, value)
    }

    fun removeItem(key: String) {
        driver.executeJavaScript<Any>(js("%s.removeItem(arguments[0])"), key)
    }

    fun clear() {
        driver.executeJavaScript<Any>(js("%s.clear()"))
    }

    @CheckReturnValue
    fun size(): Int {
        return driver.executeJavaScript<Any>(js("return %s.length")).toString().toInt()
    }

    @get:CheckReturnValue
    val isEmpty: Boolean
        get() = size() == 0

    private fun js(jsCodeTemplate: String): String {
        return String.format(jsCodeTemplate, storage)
    }
}

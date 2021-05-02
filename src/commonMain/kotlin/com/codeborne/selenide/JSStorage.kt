package com.codeborne.selenide


abstract class JSStorage(private val driver: Driver, private val storage: String) {
    fun containsItem(key: String): Boolean {
        return getItem(key) != null
    }
    fun getItem(key: String): String? {
        return driver.executeJavaScript("return $storage.getItem(arguments[0])", key)
    }

    fun setItem(key: String, value: String) {
        driver.executeJavaScript<Any>("$storage.setItem(arguments[0], arguments[1])", key, value)
    }

    fun removeItem(key: String) {
        driver.executeJavaScript<Any>("$storage.removeItem(arguments[0])", key)
    }

    fun clear() {
        driver.executeJavaScript<Any>("$storage.clear()")
    }
    fun size(): Int {
        return driver.executeJavaScript<Any>("return $storage.length").toString().toInt()
    }
    val isEmpty: Boolean
        get() = size() == 0
}

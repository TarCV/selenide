package com.codeborne.selenide


abstract class JSStorage(private val driver: Driver, private val storage: String) {
    suspend fun containsItem(key: String): Boolean {
        return getItem(key) != null
    }
    suspend fun getItem(key: String): String? {
        return driver.executeJavaScript("return $storage.getItem(arguments[0])", key)
    }

    suspend fun setItem(key: String, value: String) {
        driver.executeJavaScript<Any>("$storage.setItem(arguments[0], arguments[1])", key, value)
    }

    suspend fun removeItem(key: String) {
        driver.executeJavaScript<Any>("$storage.removeItem(arguments[0])", key)
    }

    suspend fun clear() {
        driver.executeJavaScript<Any>("$storage.clear()")
    }
    suspend fun size(): Int {
        return driver.executeJavaScript<Any>("return $storage.length").toString().toInt()
    }

    suspend fun isEmpty(): Boolean = size() == 0
}

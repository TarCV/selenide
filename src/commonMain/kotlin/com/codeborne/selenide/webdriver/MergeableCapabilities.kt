package com.codeborne.selenide.webdriver

import org.openqa.selenium.Capabilities
import org.openqa.selenium.MutableCapabilities

/**
 * A subclass of MutableCapabilities which has fixed `merge` method:
 * it can properly merge all these ChromeOptions etc. with their Maps inside of Maps.
 */
class MergeableCapabilities(base: org.openqa.selenium.Capabilities, extraCapabilities: org.openqa.selenium.Capabilities) : org.openqa.selenium.MutableCapabilities() {
    private fun areDifferent(text1: String, text2: String): Boolean {
        return text1.isNotEmpty() && text2.isNotEmpty() && text1 != text2
    }

    override fun setCapability(key: String, value: Any?) {
        if (value is Map<*, *>) {
            setCapabilityMap(key, value as Map<String, Any>)
        } else {
            super.setCapability(key, value)
        }
    }

    private fun setCapabilityMap(key: String, value: Map<String, Any>) {
        val previousValue = getCapability(key)
        when (previousValue) {
            null -> {
                super.setCapability(key, value)
            }
            is Map<*, *> -> {
                super.setCapability(key, mergeMaps(previousValue as Map<String, Any>, value))
            }
            else -> {
                throw IllegalArgumentException(
                    "Cannot merge capability " + key + " of different types: " +
                            value::class.simpleName + " vs " + previousValue::class.simpleName
                )
            }
        }
    }

    private fun mergeMaps(base: Map<String, Any>, extra: Map<String, Any>): Map<String, Any> {
        val result: MutableMap<String, Any> = HashMap()
        for ((key, baseValue) in base) {
            val extraValue = extra[key]
            result[key] = merge(baseValue, extraValue)
        }
        for ((key, value) in extra) {
            if (!result.containsKey(key)) {
                result[key] = value
            }
        }
        return result
    }

    private fun merge(baseValue: Any, extraValue: Any?): Any {
        return if (extraValue == null) {
            baseValue
        } else if (baseValue is List<*> && extraValue is List<*>) {
            mergeLists(baseValue as List<Any>, extraValue as List<Any>)
        } else if (baseValue is Array<*> && extraValue is Array<*>) {
            mergeArrays(baseValue as Array<Any>, extraValue as Array<Any>)
        } else if (baseValue is Array<*> && extraValue is List<*>) {
            mergeLists(
                listOf(*baseValue as Array<Any>),
                extraValue as List<Any>
            )
        } else if (baseValue is List<*> && extraValue is Array<*>) {
            mergeLists(
                baseValue as List<Any>,
                listOf(*extraValue as Array<Any>)
            )
        } else if (baseValue::class != extraValue::class) {
            throw IllegalArgumentException("Cannot merge values of different types: $baseValue vs $extraValue")
        } else {
            extraValue
        }
    }

    private fun mergeLists(base: List<Any>, extra: List<Any>): List<Any> {
        val result = ArrayList<Any>()
        result.addAll(base)
        result.addAll(extra)
        return result
    }

    private fun mergeArrays(base: Array<Any>, extra: Array<Any>): Array<Any?> {
        val result = arrayOfNulls<Any>(base.size + extra.size)
        base.copyInto(result, 0, 0, base.size)
        extra.copyInto(result, base.size, 0)
        return result
    }

    init {
        require(!areDifferent(base.browserName, extraCapabilities.browserName)) {
                "Conflicting browser name: '${base.browserName}' vs. '${extraCapabilities.browserName}'"
        }
        merge(base)
        merge(extraCapabilities)
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.lighthousegames.logging.logging
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.UnsupportedCommandException
import org.openqa.selenium.WebDriverException

class Describe(private val driver: Driver, private val element: org.openqa.selenium.WebElement) {
    private var _sb: StringBuilder? = null

    private suspend fun initIfNeeded(): StringBuilder {
        return _sb.let {
            if (it == null) {
                val newBuilder = StringBuilder().apply {
                    append('<')
                        .append(element.getTagName())
                }
                _sb = newBuilder
                newBuilder
            } else {
                it
            }
        }
    }

    suspend fun appendAttributes(): Describe {
        try {
            if (supportsJavascriptAttributes()) {
                return appendAllAttributes()
            }
        } catch (browserDoesNotSupportJavaScript: org.openqa.selenium.NoSuchElementException) {
            // ignore
        } catch (browserDoesNotSupportJavaScript: UnsupportedOperationException) {
        } catch (browserDoesNotSupportJavaScript: org.openqa.selenium.UnsupportedCommandException) {
        } catch (browserDoesNotSupportJavaScript: org.openqa.selenium.StaleElementReferenceException) {
        } catch (probablyBrowserDoesNotSupportJavaScript: org.openqa.selenium.WebDriverException) {
            if (probablyBrowserDoesNotSupportJavaScript.message?.toLowerCase()
                    ?.contains("method is not implemented") != true
            ) {
                log.warn { "Failed to get attributes via JS: $probablyBrowserDoesNotSupportJavaScript" }
            }
        }
        return appendPredefinedAttributes()
    }

    private suspend fun appendAllAttributes(): Describe {
        val map = driver.executeJavaScript<Map<String, String?>?>(
            "var s = {};" +
                    "var attrs = arguments[0].attributes;" +
                    "for (var i = 0; i < attrs.length; i++) {" +
                    "   var a = attrs[i]; " +
                    "   if (a.name != 'style') {" +
                    "     s[a.name] = a.value;" +
                    "   }" +
                    "}" +
                    "return s;", element
        ).let { it?.toMutableMap() ?: mutableMapOf() }

        map["value"] = element.getAttribute("value")
        if (!map.containsKey("type")) {
            map["type"] = element.getAttribute("type")
        }

        val sortedByName = map
            .entries
            .sortedBy { it.key }

        for ((key, value) in sortedByName) {
            attr(key, value)
        }
        return this
    }

    private suspend fun appendPredefinedAttributes(): Describe {
        return attr("class").attr("disabled").attr("readonly").attr("href").attr("id").attr("name")
            .attr("onclick").attr("onchange").attr("placeholder")
            .attr("type").attr("value")
    }

    private fun supportsJavascriptAttributes(): Boolean {
        return driver.supportsJavascript()
    }

    suspend fun attr(attributeName: String): Describe {
        return try {
            val attributeValue = element.getAttribute(attributeName)
            attr(attributeName, attributeValue)
        } catch (browserDoesNotSupportJavaScript: org.openqa.selenium.NoSuchElementException) {
            this
        } catch (browserDoesNotSupportJavaScript: UnsupportedOperationException) {
            this
        } catch (browserDoesNotSupportJavaScript: org.openqa.selenium.UnsupportedCommandException) {
            this
        } catch (browserDoesNotSupportJavaScript: org.openqa.selenium.StaleElementReferenceException) {
            this
        } catch (probablyBrowserDoesNotSupportJavaScript: org.openqa.selenium.WebDriverException) {
            if (probablyBrowserDoesNotSupportJavaScript.message?.toLowerCase()
                    ?.contains("method is not implemented") != true
            ) {
                log.warn { "Failed to get attribute ${attributeName}: $probablyBrowserDoesNotSupportJavaScript" }
            }
            this
        }
    }

    private suspend fun attr(attributeName: String, attributeValue: String?): Describe {
        if (attributeValue != null) {
            val sb = initIfNeeded()

            if (attributeValue.isNotEmpty()) {
                sb.append(' ').append(attributeName).append("=\"").append(attributeValue).append('"')
            } else {
                sb.append(' ').append(attributeName)
            }
        }
        return this
    }

    suspend fun serialize(): String {
        val sb = initIfNeeded()
        val text = safeCall("text") { element.getText() }
        sb.append('>').append(text ?: "").append("</").append(safeCall("tagName") { element.getTagName() }).append('>')
        return sb.toString()
    }

    override fun toString(): String = throw UnsupportedOperationException()

    suspend fun flush(): String {
        val sb = initIfNeeded()
        return sb.append('>').toString()
    }

    suspend fun isSelected(element: org.openqa.selenium.WebElement): Describe {
        try {
            if (element.isSelected()) {
                val sb = initIfNeeded()
                sb.append(' ').append("selected:true")
            }
        } catch (ignore: UnsupportedOperationException) {
        } catch (ignore: org.openqa.selenium.WebDriverException) {
        }
        return this
    }

    suspend fun isDisplayed(element: org.openqa.selenium.WebElement): Describe {
        val sb = initIfNeeded()
        try {
            if (!element.isDisplayed()) {
                sb.append(' ').append("displayed:false")
            }
        } catch (e: UnsupportedOperationException) {
            // TODO: was debug in Java
            log.warn(e) { "Failed to check visibility" }
            sb.append(' ').append("displayed:").append(Cleanup.of.webdriverExceptionMessage(e))
        } catch (e: org.openqa.selenium.WebDriverException) {
            // TODO: was debug in Java
            log.warn(e) { "Failed to check visibility" }
            sb.append(' ').append("displayed:").append(Cleanup.of.webdriverExceptionMessage(e))
        } catch (e: RuntimeException) {
            log.error(e) { "Failed to check visibility" }
            sb.append(' ').append("displayed:").append(Cleanup.of.webdriverExceptionMessage(e))
        }
        return this
    }

    private inline fun safeCall(name: String, method: () -> String): String? {
        return try {
            method()
        } catch (e: org.openqa.selenium.WebDriverException) {
            // TODO: was debug in Java
            log.warn(e) { "Failed to get $name" }
            Cleanup.of.webdriverExceptionMessage(e)
        } catch (e: RuntimeException) {
            log.error(e) { "Failed to get $name" }
            "?"
        } catch (e: NullPointerException) {
            log.error(e) { "Failed to get $name" }
            "?"
        }
    }

    companion object {
        private val log = logging(Describe::class.simpleName)
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.UnsupportedCommandException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory
import java.util.SortedMap
import java.util.TreeMap
import java.util.function.Supplier
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Describe(private val driver: Driver, private val element: WebElement) {
    private val sb = StringBuilder()
    fun appendAttributes(): Describe {
        try {
            if (supportsJavascriptAttributes()) {
                return appendAllAttributes()
            }
        } catch (browserDoesNotSupportJavaScript: NoSuchElementException) {
            // ignore
        } catch (browserDoesNotSupportJavaScript: UnsupportedOperationException) {
        } catch (browserDoesNotSupportJavaScript: UnsupportedCommandException) {
        } catch (browserDoesNotSupportJavaScript: StaleElementReferenceException) {
        } catch (probablyBrowserDoesNotSupportJavaScript: WebDriverException) {
            if (!probablyBrowserDoesNotSupportJavaScript.message!!.toLowerCase()
                    .contains("method is not implemented")
            ) {
                log.warn("Failed to get attributes via JS: {}", probablyBrowserDoesNotSupportJavaScript.toString())
            }
        }
        return appendPredefinedAttributes()
    }

    private fun appendAllAttributes(): Describe {
        val map = driver.executeJavaScript<Map<String, String>>(
            "var s = {};" +
                    "var attrs = arguments[0].attributes;" +
                    "for (var i = 0; i < attrs.length; i++) {" +
                    "   var a = attrs[i]; " +
                    "   if (a.name != 'style') {" +
                    "     s[a.name] = a.value;" +
                    "   }" +
                    "}" +
                    "return s;", element
        )
        val sortedByName: SortedMap<String, String> = TreeMap()
        if (map != null) {
            sortedByName.putAll(map)
        }
        sortedByName["value"] = element.getAttribute("value")
        if (!sortedByName.containsKey("type")) {
            sortedByName["type"] = element.getAttribute("type")
        }
        for ((key, value) in sortedByName) {
            attr(key, value)
        }
        return this
    }

    private fun appendPredefinedAttributes(): Describe {
        return attr("class").attr("disabled").attr("readonly").attr("href").attr("id").attr("name")
            .attr("onclick").attr("onchange").attr("placeholder")
            .attr("type").attr("value")
    }

    private fun supportsJavascriptAttributes(): Boolean {
        return driver.supportsJavascript()
    }

    fun attr(attributeName: String): Describe {
        return try {
            val attributeValue = element.getAttribute(attributeName)
            attr(attributeName, attributeValue)
        } catch (browserDoesNotSupportJavaScript: NoSuchElementException) {
            this
        } catch (browserDoesNotSupportJavaScript: UnsupportedOperationException) {
            this
        } catch (browserDoesNotSupportJavaScript: UnsupportedCommandException) {
            this
        } catch (browserDoesNotSupportJavaScript: StaleElementReferenceException) {
            this
        } catch (probablyBrowserDoesNotSupportJavaScript: WebDriverException) {
            if (!probablyBrowserDoesNotSupportJavaScript.message!!.toLowerCase()
                    .contains("method is not implemented")
            ) {
                log.warn(
                    "Failed to get attribute {}: {}",
                    attributeName,
                    probablyBrowserDoesNotSupportJavaScript.toString()
                )
            }
            this
        }
    }

    private fun attr(attributeName: String, attributeValue: String?): Describe {
        if (attributeValue != null) {
            if (attributeValue.isNotEmpty()) {
                sb.append(' ').append(attributeName).append("=\"").append(attributeValue).append('"')
            } else {
                sb.append(' ').append(attributeName)
            }
        }
        return this
    }

    fun serialize(): String {
        val text = safeCall("text") { element.text }
        sb.append('>').append(text ?: "").append("</").append(safeCall("tagName") { element.tagName }).append('>')
        return sb.toString()
    }

    @CheckReturnValue
    override fun toString(): String {
        return sb.toString()
    }

    fun flush(): String {
        return sb.append('>').toString()
    }

    fun isSelected(element: WebElement): Describe {
        try {
            if (element.isSelected) {
                sb.append(' ').append("selected:true")
            }
        } catch (ignore: UnsupportedOperationException) {
        } catch (ignore: WebDriverException) {
        }
        return this
    }

    fun isDisplayed(element: WebElement): Describe {
        try {
            if (!element.isDisplayed) {
                sb.append(' ').append("displayed:false")
            }
        } catch (e: UnsupportedOperationException) {
            log.debug("Failed to check visibility", e)
            sb.append(' ').append("displayed:").append(Cleanup.of.webdriverExceptionMessage(e))
        } catch (e: WebDriverException) {
            log.debug("Failed to check visibility", e)
            sb.append(' ').append("displayed:").append(Cleanup.of.webdriverExceptionMessage(e))
        } catch (e: RuntimeException) {
            log.error("Failed to check visibility", e)
            sb.append(' ').append("displayed:").append(Cleanup.of.webdriverExceptionMessage(e))
        }
        return this
    }

    private fun safeCall(name: String, method: Supplier<String>): String? {
        return try {
            method.get()
        } catch (e: WebDriverException) {
            log.debug("Failed to get {}", name, e)
            Cleanup.of.webdriverExceptionMessage(e)
        } catch (e: RuntimeException) {
            log.error("Failed to get {}", name, e)
            "?"
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Describe::class.java)
    }

    init {
        sb.append('<').append(element.tagName)
    }
}

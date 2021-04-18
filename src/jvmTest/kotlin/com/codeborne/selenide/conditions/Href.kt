package com.codeborne.selenide.conditions

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import java.io.UnsupportedEncodingException
import support.URLDecoder
import java.nio.charset.StandardCharsets

class Href(expectedAttributeValue: String) : AttributeWithValue("href", expectedAttributeValue) {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        val href = getAttributeValue(element)
        val fullUrl = decode(href)
        return fullUrl.endsWith(expectedAttributeValue) ||
                fullUrl.endsWith("$expectedAttributeValue/") ||
                href.endsWith(expectedAttributeValue)
    }

    fun decode(url: String): String {
        return try {
            URLDecoder.decode(url, StandardCharsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("Failed to decode $url", e)
        }
    }
}

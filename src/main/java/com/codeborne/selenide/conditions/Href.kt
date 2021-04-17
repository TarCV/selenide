package com.codeborne.selenide.conditions

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Href(expectedAttributeValue: String) : AttributeWithValue("href", expectedAttributeValue) {
    @CheckReturnValue
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

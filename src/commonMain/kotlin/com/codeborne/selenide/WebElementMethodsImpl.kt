package com.codeborne.selenide

import com.codeborne.selenide.impl.SelenideElementProxy
import com.codeborne.selenide.impl.SelenideElementProxy.Companion.NO_ARGS
import okio.ExperimentalFileSystem
import org.openqa.selenium.interactions.Coordinates
import org.openqa.selenium.Rectangle
import org.openqa.selenium.Dimension
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.OutputType
import org.openqa.selenium.Point
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.internal.HasIdentity
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalFileSystem
// TODO: remove runBlocking from this class
class WebElementMethodsImpl(val proxy: SelenideElementProxy) : WebElementMethods {
    override suspend fun isDisplayed(): Boolean {
        return proxy.webElementInvoke(WebElement::isDisplayed, NO_ARGS)
    }
    override suspend fun isEnabled(): Boolean {
        return proxy.webElementInvoke(WebElement::isEnabled, NO_ARGS)
    }
    override suspend fun isSelected(): Boolean {
        return proxy.webElementInvoke(WebElement::isSelected, NO_ARGS)
    }
    override suspend fun getCoordinates(): Coordinates {
        return proxy.webElementInvoke(org.openqa.selenium.interactions.Locatable::getCoordinates, NO_ARGS)
    }
    override suspend fun getId(): String {
        return proxy.webElementInvoke(HasIdentity::getId, NO_ARGS)
    }
    override suspend fun getRect(): Rectangle {
        return proxy.webElementInvoke(WebElement::getRect, NO_ARGS)
    }
    override suspend fun getSize(): Dimension {
        return proxy.webElementInvoke(WebElement::getSize, NO_ARGS)
    }
    override suspend fun getTagName(): String {
        return proxy.webElementInvoke(WebElement::getTagName, NO_ARGS)
    }
    override suspend fun getText(): String {
        return proxy.webElementInvoke(WebElement::getText, NO_ARGS)
    }

    override suspend fun clear() {
        return proxy.webElementInvoke(WebElement::clear, NO_ARGS)
    }
    override suspend fun click() {
        return proxy.webElementInvoke(WebElement::click, NO_ARGS)
    }

    override suspend fun findElement(by: By): WebElement {
        return proxy.webElementInvoke(WebElement::findElement, arrayOf(by))
    }
    override suspend fun findElements(by: By): List<WebElement> {
        return proxy.webElementInvoke(WebElement::findElements, arrayOf(by))
    }
    override suspend fun getAttribute(name: String): String? {
        return proxy.webElementInvoke(WebElement::getAttribute, arrayOf(name))
    }
    override suspend fun getCssValue(propertyName: String): String {
        return proxy.webElementInvoke(WebElement::getCssValue, arrayOf(propertyName))
    }
    override suspend fun getLocation(): Point {
        return proxy.webElementInvoke(WebElement::getLocation, NO_ARGS)
    }
    override suspend fun <T : OutputType<T>> getScreenshotAs(type: OutputType<T>): T {
        return proxy.webElementInvoke(TakesScreenshot::getScreenshotAs, arrayOf(type))
    }
    override suspend fun sendKeys(vararg keys: CharSequence) {
        return proxy.webElementInvoke(arrayOf(keys), "sendKeys") { element, actualArgs ->
            element.sendKeys(*(actualArgs[0] as Array<out CharSequence>))
        }
    }
    override suspend fun sendKeys(keys: Keys) {
        return proxy.webElementInvoke(arrayOf(keys), "sendKeys") { element, actualArgs ->
            element.sendKeys(actualArgs[0] as Keys)
        }
    }
    override suspend fun submit() {
        return proxy.webElementInvoke(WebElement::submit, NO_ARGS)
    }

    override fun equals(other: Any?): Boolean = throw UnsupportedOperationException()

    override fun hashCode() = throw UnsupportedOperationException()
}

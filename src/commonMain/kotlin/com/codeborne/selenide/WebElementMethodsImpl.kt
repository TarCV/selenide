package com.codeborne.selenide

import com.codeborne.selenide.impl.SelenideElementProxy
import com.codeborne.selenide.impl.SelenideElementProxy.Companion.NO_ARGS
import kotlinx.coroutines.runBlocking
import okio.ExperimentalFileSystem
import org.openqa.selenium.interactions.Coordinates
import org.openqa.selenium.Rectangle
import org.openqa.selenium.Dimension
import org.openqa.selenium.By
import org.openqa.selenium.OutputType
import org.openqa.selenium.Point
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalFileSystem
// TODO: remove runBlocking from this class
class WebElementMethodsImpl(val proxy: SelenideElementProxy) : WebElementMethods {
    override fun isDisplayed(): Boolean = runBlocking {
        proxy.webElementInvoke(this, WebElement::isDisplayed, NO_ARGS)
    }
    override fun isEnabled(): Boolean = runBlocking {
        proxy.webElementInvoke(this, WebElement::isEnabled, NO_ARGS)
    }
    override fun isSelected(): Boolean = runBlocking {
        proxy.webElementInvoke(this, WebElement::isSelected, NO_ARGS)
    }
    override fun getCoordinates(): Coordinates = runBlocking {
        proxy.webElementInvoke(this, org.openqa.selenium.interactions.Locatable::getCoordinates, NO_ARGS)
    }
    override fun getId(): String = runBlocking {
        proxy.webElementInvoke(this, org.openqa.selenium.internal.HasIdentity::getId, NO_ARGS)
    }
    override fun getRect(): Rectangle = runBlocking {
        proxy.webElementInvoke(this, WebElement::getRect, NO_ARGS)
    }
    override fun getSize(): Dimension = runBlocking {
        proxy.webElementInvoke(this, WebElement::getSize, NO_ARGS)
    }
    override fun getTagName(): String = runBlocking {
        proxy.webElementInvoke(this, WebElement::getTagName, NO_ARGS)
    }
    override fun getText(): String = runBlocking {
        proxy.webElementInvoke(this, WebElement::getText, NO_ARGS)
    }
    override fun getWrappedDriver(): WebDriver = runBlocking {
        proxy.webElementInvoke(this, org.openqa.selenium.WrapsDriver::getWrappedDriver, NO_ARGS)
    }

    override fun clear() = runBlocking {
        proxy.webElementInvoke(this, WebElement::clear, NO_ARGS)
    }
    override fun click() = runBlocking {
        proxy.webElementInvoke(this, WebElement::click, NO_ARGS)
    }

    override fun findElement(by: By): WebElement = runBlocking {
        proxy.webElementInvoke(this, WebElement::findElement, arrayOf(by))
    }
    override fun findElements(by: By): List<WebElement> = runBlocking {
        proxy.webElementInvoke(this, WebElement::findElements, arrayOf(by))
    }
    override fun getAttribute(name: String): String? = runBlocking {
        proxy.webElementInvoke(this, WebElement::getAttribute, arrayOf(name))
    }
    override fun getCssValue(propertyName: String): String = runBlocking {
        proxy.webElementInvoke(this, WebElement::getCssValue, arrayOf(propertyName))
    }
    override fun getLocation(): Point = runBlocking {
        proxy.webElementInvoke(this, WebElement::getLocation, NO_ARGS)
    }
    override fun <T : Any> getScreenshotAs(type: OutputType<T>): T = runBlocking {
        proxy.webElementInvoke(this, WebElement::getScreenshotAs, arrayOf(type))
    }
    override fun sendKeys(vararg keys: CharSequence) = runBlocking {
        proxy.webElementInvoke(this, WebElement::sendKeys, arrayOf(keys))
    }
    override fun submit() = runBlocking {
        proxy.webElementInvoke(this, WebElement::submit, NO_ARGS)
    }

    override fun equals(other: Any?): Boolean = runBlocking {
        proxy.webElementInvoke(this, WebElement::equals, arrayOf(other))
    }

    override fun hashCode(): Int = runBlocking {
        proxy.webElementInvoke(this, WebElement::hashCode, NO_ARGS)
    }
}

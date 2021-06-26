package com.codeborne.selenide

import com.codeborne.selenide.impl.SelenideElementProxy
import okio.ExperimentalFileSystem
import okio.Path
import kotlinx.coroutines.runBlocking
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebElement
import org.openqa.selenium.WrapsDriver
import org.openqa.selenium.interactions.Locatable
import org.openqa.selenium.internal.HasIdentity
import org.openqa.selenium.internal.WrapsElement
import kotlin.jvm.JvmSynthetic
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Wrapper around [WebElement] with additional methods like
 * [.shouldBe] and [.shouldHave]
 */
@ExperimentalFileSystem
@ExperimentalTime
class SelenideElement private constructor(
    proxy: SelenideElementProxy,
    private val selenideElementMethodsImpl: SelenideElementMethodsImpl
) :
    WebElementMethods by WebElementMethodsImpl(proxy),
    SelenideElementMethods by selenideElementMethodsImpl,
    WrapsElement
{
    constructor(proxy: SelenideElementProxy): this(
        proxy, SelenideElementMethodsImpl(proxy)
    )

    init {
        selenideElementMethodsImpl.linkElement(this)
    }

    override fun getWrappedElement(): WebElement = runBlocking {
        selenideElementMethodsImpl.getWrappedElementAsync()
    }

    suspend fun getWrappedElement(unused: Nothing? = null): WebElement = selenideElementMethodsImpl.getWrappedElementAsync()

    @Deprecated(
        replaceWith = ReplaceWith("getWrappedElement"),
        level = DeprecationLevel.HIDDEN,
        message = ""
    )
    override suspend fun getWrappedElementAsync(): WebElement = selenideElementMethodsImpl.getWrappedElementAsync()

    @Deprecated(
        replaceWith = ReplaceWith("click"),
        level = DeprecationLevel.HIDDEN,
        message = ""
    )
    override suspend fun clickAsync() = selenideElementMethodsImpl.clickAsync()

    @Deprecated(
        replaceWith = ReplaceWith("getText"),
        level = DeprecationLevel.HIDDEN,
        message = ""
    )
    override suspend fun getTextAsync(): String = selenideElementMethodsImpl.getTextAsync()

    @Deprecated(
        replaceWith = ReplaceWith("getAttribute"),
        level = DeprecationLevel.HIDDEN,
        message = ""
    )
    override suspend fun getAttributeAsync(name: String): String? = selenideElementMethodsImpl.getAttributeAsync(name)

    @Deprecated(
        replaceWith = ReplaceWith("getCssValue"),
        level = DeprecationLevel.HIDDEN,
        message = ""
    )
    override suspend fun getCssValueAsync(propertyName: String): String = selenideElementMethodsImpl.getCssValueAsync(propertyName)

    @Deprecated(
        replaceWith = ReplaceWith("isDisplayed"),
        level = DeprecationLevel.HIDDEN,
        message = ""
    )
    override suspend fun isDisplayedAsync(): Boolean = selenideElementMethodsImpl.isDisplayedAsync()
}

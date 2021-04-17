package com.codeborne.selenide

import com.codeborne.selenide.proxy.SelenideProxyServer
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import javax.annotation.CheckReturnValue

interface Driver {
    @CheckReturnValue
    fun config(): Config

    @CheckReturnValue
    fun browser(): Browser

    @CheckReturnValue
    fun hasWebDriverStarted(): Boolean

    @get:CheckReturnValue
    val webDriver: WebDriver

    @get:CheckReturnValue
    val proxy: SelenideProxyServer?

    @get:CheckReturnValue
    val getAndCheckWebDriver: WebDriver

    @CheckReturnValue
    fun browserDownloadsFolder(): DownloadsFolder?
    fun close()

    @CheckReturnValue
    @JvmDefault
    fun supportsJavascript(): Boolean {
        return hasWebDriverStarted() && webDriver is JavascriptExecutor
    }

    fun <T> executeJavaScript(jsCode: String, vararg arguments: Any): T {
        return (webDriver as JavascriptExecutor).executeScript(jsCode, *arguments) as T
    }

    fun <T> executeAsyncJavaScript(jsCode: String, vararg arguments: Any): T {
        return (webDriver as JavascriptExecutor).executeAsyncScript(jsCode, *arguments) as T
    }

    @JvmDefault
    fun clearCookies() {
        if (hasWebDriverStarted()) {
            webDriver.manage().deleteAllCookies()
        }
    }

    @get:CheckReturnValue
    val userAgent: String
        get() = executeJavaScript("return navigator.userAgent;")

    @CheckReturnValue
    @JvmDefault
    fun source(): String {
        return webDriver.pageSource
    }

    @CheckReturnValue
    @JvmDefault
    fun url(): String {
        return webDriver.currentUrl
    }

    @CheckReturnValue
    @JvmDefault
    fun getCurrentFrameUrl(): String = executeJavaScript<Any>("return window.location.href").toString()

    @CheckReturnValue
    @JvmDefault
    fun switchTo(): SelenideTargetLocator {
        return SelenideTargetLocator(this)
    }

    @CheckReturnValue
    @JvmDefault
    fun actions(): Actions {
        return Actions(webDriver)
    }
}

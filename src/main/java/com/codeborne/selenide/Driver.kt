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
    fun supportsJavascript(): Boolean {
        return hasWebDriverStarted() && webDriver is JavascriptExecutor
    }

    fun <T> executeJavaScript(jsCode: String, vararg arguments: Any): T {
        return (webDriver as JavascriptExecutor).executeScript(jsCode, *arguments) as T
    }

    fun <T> executeAsyncJavaScript(jsCode: String, vararg arguments: Any): T {
        return (webDriver as JavascriptExecutor).executeAsyncScript(jsCode, *arguments) as T
    }

    fun clearCookies() {
        if (hasWebDriverStarted()) {
            webDriver.manage().deleteAllCookies()
        }
    }

    @get:CheckReturnValue
    val userAgent: String
        get() = executeJavaScript("return navigator.userAgent;")

    @CheckReturnValue
    fun source(): String {
        return webDriver.pageSource
    }

    @CheckReturnValue
    fun url(): String {
        return webDriver.currentUrl
    }

    @get:CheckReturnValue
    val currentFrameUrl: String
        get() = executeJavaScript<Any>("return window.location.href").toString()

    @CheckReturnValue
    fun switchTo(): SelenideTargetLocator {
        return SelenideTargetLocator(this)
    }

    @CheckReturnValue
    fun actions(): Actions {
        return Actions(webDriver)
    }
}

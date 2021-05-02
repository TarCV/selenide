package com.codeborne.selenide

import okio.ExperimentalFileSystem
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions

interface Driver {
    fun config(): Config
    fun browser(): Browser
    fun hasWebDriverStarted(): Boolean
    val webDriver: org.openqa.selenium.WebDriver
// TODO:   val proxy: SelenideProxyServer?
    val getAndCheckWebDriver: org.openqa.selenium.WebDriver

    @ExperimentalFileSystem
    fun browserDownloadsFolder(): DownloadsFolder?

    fun close()
    fun supportsJavascript(): Boolean {
        return hasWebDriverStarted() && webDriver is org.openqa.selenium.JavascriptExecutor
    }

    fun <T> executeJavaScript(jsCode: String, vararg arguments: Any): T {
        return (webDriver as org.openqa.selenium.JavascriptExecutor).executeScript(jsCode, *arguments) as T
    }

    fun <T> executeAsyncJavaScript(jsCode: String, vararg arguments: Any): T {
        return (webDriver as org.openqa.selenium.JavascriptExecutor).executeAsyncScript(jsCode, *arguments) as T
    }

    suspend fun clearCookies() {
        if (hasWebDriverStarted()) {
            webDriver.manage().deleteAllCookies()
        }
    }
    val userAgent: String
        get() = executeJavaScript("return navigator.userAgent;")
    fun source(): String {
        return webDriver.pageSource
    }
    fun url(): String {
        return webDriver.currentUrl
    }
    fun getCurrentFrameUrl(): String = executeJavaScript<Any>("return window.location.href").toString()
    fun switchTo(): SelenideTargetLocator {
        return SelenideTargetLocator(this)
    }
    fun actions(): org.openqa.selenium.interactions.Actions {
        return org.openqa.selenium.interactions.Actions(webDriver)
    }
}

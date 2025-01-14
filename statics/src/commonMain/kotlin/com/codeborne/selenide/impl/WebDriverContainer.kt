package com.codeborne.selenide.impl

import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.WebDriverRunner
import com.google.errorprone.annotations.CheckReturnValue
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.WebDriverEventListener
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
interface WebDriverContainer {
    fun addListener(listener: WebDriverEventListener)
    fun setWebDriver(webDriver: WebDriver, selenideProxy: Nothing?)

    @okio.ExperimentalFileSystem
    fun setWebDriver(
        webDriver: WebDriver,
        selenideProxy: Nothing?,
        browserDownloadsFolder: DownloadsFolder
    )

    fun resetWebDriver()

    @get:CheckReturnValue
    var webDriver: WebDriver

    /**
 * Get selenide proxy. It's activated only if Configuration.proxyEnabled == true
 *
 * @return null if proxy server is not started
 */
/* TODO:
    @get:CheckReturnValue
    val proxyServer: SelenideProxyServer?
    get() {
        return WebDriverRunner.webdriverContainer.proxyServer
    }
*/
    fun setProxy(webProxy: Proxy?)

    @get:CheckReturnValue
    val andCheckWebDriver: WebDriver

    @get:CheckReturnValue
    @okio.ExperimentalFileSystem
    val browserDownloadsFolder: DownloadsFolder

    fun closeWindow()
    fun closeWebDriver()
    fun hasWebDriverStarted(): Boolean
    fun clearBrowserCache()

    @get:CheckReturnValue
    val pageSource: String

    @get:CheckReturnValue
    val currentUrl: String

    @CheckReturnValue
    suspend fun getCurrentFrameUrl(): String
}

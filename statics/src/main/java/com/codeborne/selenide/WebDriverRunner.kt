package com.codeborne.selenide

import com.codeborne.selenide.Configuration.browser
import com.codeborne.selenide.Configuration.headless
import com.codeborne.selenide.impl.ThreadLocalSelenideDriver
import com.codeborne.selenide.impl.WebDriverContainer
import com.codeborne.selenide.impl.WebDriverThreadLocalContainer
import com.codeborne.selenide.proxy.SelenideProxyServer
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.WebDriverEventListener
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

/**
 * A static facade for accessing WebDriver instance for current threads
 */
@ParametersAreNonnullByDefault
object WebDriverRunner : Browsers {
    var webdriverContainer: WebDriverContainer = WebDriverThreadLocalContainer()

    @get:CheckReturnValue
    val selenideDriver: SelenideDriver = ThreadLocalSelenideDriver()

    /**
     * Use this method BEFORE opening a browser to add custom event listeners to webdriver.
     *
     * @param listener your listener of webdriver events
     */
    fun addListener(listener: WebDriverEventListener) {
        webdriverContainer.addListener(listener)
    }

    fun setWebDriver(webDriver: WebDriver, selenideProxy: SelenideProxyServer) {
        webdriverContainer.setWebDriver(webDriver, selenideProxy)
    }

    fun setWebDriver(
        webDriver: WebDriver,
        selenideProxy: SelenideProxyServer,
        browserDownloadsFolder: DownloadsFolder
    ) {
        webdriverContainer.setWebDriver(webDriver, selenideProxy, browserDownloadsFolder)
    }
    /**
     * Get the underlying instance of Selenium WebDriver.
     * This can be used for any operations directly with WebDriver.
     */
    /**
     * Tell Selenide use your provided WebDriver instance.
     * Use it if you need a custom logic for creating WebDriver.
     *
     *
     * It's recommended not to use implicit wait with this driver, because Selenide handles timing issues explicitly.
     *
     * <br></br>
     *
     *
     * NB! Be sure to call this method before calling `open(url)`.
     * Otherwise Selenide will create its own WebDriver instance and would not close it.
     *
     *
     *
     * NB! When using your custom webdriver, you are responsible for closing it.
     * Selenide will not take care of it.
     *
     *
     *
     *
     * NB! Webdriver instance should be created and used in the same thread.
     * A typical error is to create webdriver instance in one thread and use it in another.
     * Selenide does not support it.
     * If you really need using multiple threads, please use #com.codeborne.selenide.WebDriverProvider
     *
     *
     *
     *
     * P.S. Alternatively, you can run tests with system property
     * <pre>  -Dbrowser=com.my.WebDriverFactory</pre>
     *
     *
     * which should implement interface #com.codeborne.selenide.WebDriverProvider
     *
     */
    @get:CheckReturnValue
    var webDriver: WebDriver
        get() = webdriverContainer.webDriver
        set(webDriver) {
            webdriverContainer.webDriver = webDriver
        }

    /**
     * Sets Selenium Proxy instance
     */
    fun setProxy(webProxy: Proxy) {
        webdriverContainer.setProxy(webProxy)
    }

    /**
     * Get selenide proxy. It's activated only if Configuration.proxyEnabled == true
     *
     * @return null if proxy server is not started
     */
    @get:CheckReturnValue
    val selenideProxy: SelenideProxyServer?
      get() {
        return webdriverContainer.proxyServer
      }


  /**
     * Get the underlying instance of Selenium WebDriver, and assert that it's still alive.
     *
     * @return new instance of WebDriver if the previous one has been closed meanwhile.
     */
    @get:CheckReturnValue
    val getAndCheckWebDriver: WebDriver
        get() = webdriverContainer.andCheckWebDriver

    @CheckReturnValue
    fun driver(): Driver {
        return selenideDriver.driver()
    }

    @get:CheckReturnValue
    val browserDownloadsFolder: DownloadsFolder
        get() = webdriverContainer.browserDownloadsFolder

    /**
     * Close the current window, quitting the browser if it's the last window currently open.
     *
     * @see WebDriver.close
     */
    fun closeWindow() {
        webdriverContainer.closeWindow()
    }

    /**
     *
     * Close the browser if it's open.
     * <br></br>
     *
     * NB! Method quits this driver, closing every associated window.
     *
     * @see WebDriver.quit
     */
    fun closeWebDriver() {
        webdriverContainer.closeWebDriver()
    }

    /**
     * @return true if instance of Selenium WebDriver is started in current thread
     */
    @CheckReturnValue
    fun hasWebDriverStarted(): Boolean {
        return webdriverContainer.hasWebDriverStarted()
    }

    fun using(driver: WebDriver, lambda: Runnable) {
        if (hasWebDriverStarted()) {
            val previous = webDriver
            try {
                lambda.run()
            } finally {
                webDriver = previous
            }
        } else {
            webDriver = driver
            try {
                lambda.run()
            } finally {
                webdriverContainer.resetWebDriver()
            }
        }
    }

    @CheckReturnValue
    private fun browser(): Browser {
        return Browser(browser, headless)
    }


    /**
     * Is Selenide configured to use Chrome browser
     */
    @get:CheckReturnValue
    val isChrome: Boolean
        get() = browser().isChrome

    /**
     * Is Selenide configured to use headless browser
     */
    @get:CheckReturnValue
    val isHeadless: Boolean
        get() = browser().isHeadless

    /**
     * Does this browser support javascript
     */
    @CheckReturnValue
    fun supportsJavascript(): Boolean {
        return driver().supportsJavascript()
    }

    /**
     * Delete all the browser cookies
     */
    fun clearBrowserCache() {
        webdriverContainer.clearBrowserCache()
    }

    /**
     * @return the source (HTML) of current page
     */
    @CheckReturnValue
    fun source(): String {
        return webdriverContainer.pageSource
    }

    /**
     * @return the URL of current page
     */
    @CheckReturnValue
    fun url(): String {
        return webdriverContainer.currentUrl
    }

    /**
     * @return the URL of current frame
     */
    @CheckReturnValue
    fun currentFrameUrl(): String {
        return webdriverContainer.currentFrameUrl
    }
}

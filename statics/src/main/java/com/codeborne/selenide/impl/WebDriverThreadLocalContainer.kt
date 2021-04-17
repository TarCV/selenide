package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import com.codeborne.selenide.Configuration.reopenBrowserOnFail
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.SharedDownloadsFolder
import com.codeborne.selenide.drivercommands.BrowserHealthChecker
import com.codeborne.selenide.drivercommands.CloseDriverCommand
import com.codeborne.selenide.drivercommands.CreateDriverCommand
import com.codeborne.selenide.proxy.SelenideProxyServer
import com.codeborne.selenide.webdriver.WebDriverFactory
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.WebDriverEventListener
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class WebDriverThreadLocalContainer : WebDriverContainer {
    private val listeners: MutableList<WebDriverEventListener> = ArrayList()

    @JvmField
    val allWebDriverThreads: MutableCollection<Thread> = ConcurrentLinkedQueue()

    @JvmField
    val threadWebDriver: MutableMap<Long, WebDriver> = ConcurrentHashMap(4)

    private val threadProxyServer: MutableMap<Long, SelenideProxyServer> = ConcurrentHashMap(4)
    private val threadDownloadsFolder: MutableMap<Long, DownloadsFolder> = ConcurrentHashMap(4)
    private var userProvidedProxy: Proxy? = null
    private val config: Config = StaticConfig()
    private val browserHealthChecker = BrowserHealthChecker()
    private val factory = WebDriverFactory()
    private val closeDriverCommand = CloseDriverCommand()
    private val createDriverCommand = CreateDriverCommand()

    @JvmField
    val cleanupThreadStarted = AtomicBoolean(false)
    override fun addListener(listener: WebDriverEventListener) {
        listeners.add(listener)
    }

    override fun setWebDriver(webDriver: WebDriver, selenideProxy: SelenideProxyServer?) {
        setWebDriver(webDriver, selenideProxy, SharedDownloadsFolder(config.downloadsFolder()))
    }

    /**
     * Make Selenide use given webdriver [and proxy] in the current thread.
     *
     * NB! This method is meant to be called BEFORE performing any actions with web elements.
     * It does NOT close a previously opened webdriver/proxy.
     *
     * @param webDriver any webdriver created by user
     * @param selenideProxy any proxy created by user (or null if proxy is not needed)
     * @param browserDownloadsFolder downloads folder - unique for the given browser instance
     */
    override fun setWebDriver(
        webDriver: WebDriver,
        selenideProxy: SelenideProxyServer?,
        browserDownloadsFolder: DownloadsFolder
    ) {
        resetWebDriver()
        val threadId = Thread.currentThread().id
        if (selenideProxy != null) {
            threadProxyServer[threadId] = selenideProxy
        }
        threadWebDriver[threadId] = webDriver
        threadDownloadsFolder[threadId] = browserDownloadsFolder
    }

    /**
     * Remove links to webdriver/proxy, but don't close the webdriver/proxy itself.
     */
    override fun resetWebDriver() {
        val threadId = Thread.currentThread().id
        threadProxyServer.remove(threadId)
        threadWebDriver.remove(threadId)
        threadDownloadsFolder.remove(threadId)
    }

    override fun setProxy(webProxy: Proxy?) {
        this.userProvidedProxy = webProxy
    }

    /**
     * @return true iff webdriver is started in current thread
     */
    @CheckReturnValue
    override fun hasWebDriverStarted(): Boolean {
        val webDriver = threadWebDriver[Thread.currentThread().id]
        return webDriver != null
    }

    @get:CheckReturnValue
    override var webDriver: WebDriver
        get() {
            val threadId = Thread.currentThread().id
            return checkNotNull(threadWebDriver[threadId]) { "No webdriver is bound to current thread: $threadId. You need to call open(url) first." }
        }
        set(webDriver) {
            setWebDriver(webDriver, null)
        }

    @get:CheckReturnValue
    override val andCheckWebDriver: WebDriver
        get() {
            var webDriver = threadWebDriver[Thread.currentThread().id]
            if (webDriver != null && reopenBrowserOnFail && !browserHealthChecker.isBrowserStillOpen(webDriver)) {
                log.info("Webdriver has been closed meanwhile. Let's re-create it.")
                closeWebDriver()
                webDriver = createDriver()
            } else if (webDriver == null) {
                log.info(
                    "No webdriver is bound to current thread: {} - let's create a new webdriver",
                    Thread.currentThread().id
                )
                webDriver = createDriver()
            }
            return webDriver
        }

    override val browserDownloadsFolder: DownloadsFolder
        get() = checkNotNull(threadDownloadsFolder[Thread.currentThread().id])

    @CheckReturnValue
    private fun createDriver(): WebDriver {
        val result = createDriverCommand.createDriver(config, factory, userProvidedProxy, listeners)
        val threadId = Thread.currentThread().id
        threadWebDriver[threadId] = result.webDriver
        result.selenideProxyServer?.let {
            threadProxyServer[threadId] = it
        }
        result.browserDownloadsFolder?.let {
            threadDownloadsFolder[threadId] = it
        }
        if (config.holdBrowserOpen()) {
            log.info(
                "Browser and proxy will stay open due to holdBrowserOpen=true: {} -> {}, {}",
                threadId, result.webDriver, result.selenideProxyServer
            )
        } else {
            markForAutoClose(Thread.currentThread())
        }
        return result.webDriver
    }

    @get:CheckReturnValue
    override val proxyServer: SelenideProxyServer?
        get() = threadProxyServer[Thread.currentThread().id]

    override fun closeWindow() {
        webDriver.close()
    }

    /**
     * Remove links to webdriver/proxy AND close the webdriver and proxy
     */
    override fun closeWebDriver() {
        val threadId = Thread.currentThread().id
        val driver = threadWebDriver[threadId]
        val proxy = threadProxyServer[threadId]
        closeDriverCommand.close(config, driver, proxy)
        resetWebDriver()
    }

    override fun clearBrowserCache() {
        if (hasWebDriverStarted()) {
            webDriver.manage().deleteAllCookies()
        }
    }

    @get:CheckReturnValue
    override val pageSource: String
        get() = webDriver.pageSource

    @get:CheckReturnValue
    override val currentUrl: String
        get() = webDriver.currentUrl

    @get:CheckReturnValue
    override val currentFrameUrl: String
        get() = Selenide.executeJavaScript<Any>("return window.location.href").toString()

    private fun markForAutoClose(thread: Thread) {
        allWebDriverThreads.add(thread)
        if (!cleanupThreadStarted.get()) {
            synchronized(this) {
                if (!cleanupThreadStarted.get()) {
                    UnusedWebdriversCleanupThread(
                        allWebDriverThreads,
                        threadWebDriver,
                        threadProxyServer,
                        threadDownloadsFolder
                    ).start()
                    cleanupThreadStarted.set(true)
                }
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebDriverThreadLocalContainer::class.java)
    }
}

package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.Driver
import com.codeborne.selenide.drivercommands.LazyDriver
import com.codeborne.selenide.webdriver.WebDriverFactory
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.WebDriverEventListener
import org.slf4j.LoggerFactory
import javax.annotation.concurrent.GuardedBy

/**
 * A `Driver` implementation which opens browser on demand (on a first call).
 * May be created with its own config, proxy and listeners.
 */
class LazyDriver internal constructor(
  private val config: Config, private val userProvidedProxy: Proxy?, listeners: List<WebDriverEventListener>,
  factory: WebDriverFactory, browserHealthChecker: BrowserHealthChecker,
  createDriverCommand: CreateDriverCommand, closeDriverCommand: CloseDriverCommand
) : Driver {
    private val browserHealthChecker: BrowserHealthChecker
    private val factory: WebDriverFactory
    private val closeDriverCommand: CloseDriverCommand
    private val createDriverCommand: CreateDriverCommand
    private val listeners: MutableList<WebDriverEventListener> = ArrayList()
    private val browser: Browser = Browser(config.browser(), config.headless())

    private var closed = false
    private var _webDriver: WebDriver? = null
    private var browserDownloadsFolder: DownloadsFolder? = null
/*    override var proxy: *//*SelenideProxyServer*//*Nothing? = null
        get() = synchronized(this) { field }
        private set(value) = synchronized(this) {
          field = value
        }*/

    constructor(config: Config, userProvidedProxy: Proxy?, listeners: List<WebDriverEventListener>) : this(
        config, userProvidedProxy, listeners, WebDriverFactory(), BrowserHealthChecker(),
        CreateDriverCommand(), CloseDriverCommand()
    )

  override fun config(): Config {
        return config
    }

    override fun browser(): Browser {
        return browser
    }

    // TODO: why this is not sync in Java?
    override fun hasWebDriverStarted(): Boolean = synchronized(this) {
        return _webDriver != null
    }

    override val webDriver: WebDriver
      get() = synchronized(this) {
          check(!closed) { "Webdriver has been closed. You need to call open(url) to open a browser again." }
          return checkNotNull(_webDriver) {
              "No webdriver is bound to current thread: " + support.System.currentThreadId() +
                      ". You need to call open(url) first."
          }
      }
    override val getAndCheckWebDriver: WebDriver
        @kotlin.time.ExperimentalTime
        get() = synchronized(this) {
          _webDriver.let {
            if (it != null && config.reopenBrowserOnFail() && !browserHealthChecker.isBrowserStillOpen(it)) {
              log.info("Webdriver has been closed meanwhile. Let's re-create it.")
              close()
              createDriver()
            } else if (it == null) {
              log.info(
                "No webdriver is bound to current thread: {} - let's create a new webdriver",
                support.System.currentThreadId()
              )
              createDriver()
            } else {
              it
            }
          }
        }
    // TODO: why this is not sync in Java?
    override fun browserDownloadsFolder(): DownloadsFolder? = synchronized(this) {
        return browserDownloadsFolder
    }

    // TODO: why this is not sync in Java?
    @kotlin.time.ExperimentalTime
    fun createDriver(): WebDriver = synchronized(this) {
        val result = createDriverCommand.createDriver(config, factory, userProvidedProxy, listeners)
        _webDriver = result.webDriver
// TODO:        proxy = result.selenideProxyServer
        browserDownloadsFolder = result.browserDownloadsFolder
        closed = false
      return result.webDriver
    }

    // TODO: why this is not sync in Java?
    override fun close() = synchronized(this) {
        closeDriverCommand.close(config, _webDriver, null /*TODO: proxy*/)
        _webDriver = null
// TODO:        proxy = null
        browserDownloadsFolder = null
        closed = true
    }

    companion object {
        private val log = LoggerFactory.getLogger(LazyDriver::class)
    }

    init {
      this.listeners.addAll(listeners)
        this.factory = factory
        this.browserHealthChecker = browserHealthChecker
        this.closeDriverCommand = closeDriverCommand
        this.createDriverCommand = createDriverCommand
    }
}
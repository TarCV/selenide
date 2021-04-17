package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.Driver
import com.codeborne.selenide.drivercommands.WebDriverWrapper
import com.codeborne.selenide.proxy.SelenideProxyServer
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import java.util.Objects
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

/**
 * A `Driver` implementation which uses given webdriver [and proxy].
 * It doesn't open a new browser.
 * It doesn't start a new proxy.
 */
@ParametersAreNonnullByDefault
class WebDriverWrapper private constructor(
    config: Config, webDriver: WebDriver,
    selenideProxy: SelenideProxyServer?, browserDownloadsFolder: DownloadsFolder,
    browserHealthChecker: BrowserHealthChecker, closeDriverCommand: CloseDriverCommand
) : Driver {
    private val config: Config

    @get:CheckReturnValue
    override val webDriver: WebDriver

    @get:CheckReturnValue
    override val proxy: SelenideProxyServer?
    private val browserDownloadsFolder: DownloadsFolder
    private val browserHealthChecker: BrowserHealthChecker
    private val closeDriverCommand: CloseDriverCommand

    constructor(
        config: Config, webDriver: WebDriver,
        selenideProxy: SelenideProxyServer?, browserDownloadsFolder: DownloadsFolder
    ) : this(config, webDriver, selenideProxy, browserDownloadsFolder, BrowserHealthChecker(), CloseDriverCommand()) {
    }

    @CheckReturnValue
    override fun config(): Config {
        return config
    }

    @CheckReturnValue
    override fun browser(): Browser {
        return Browser(config.browser(), config.headless())
    }

    @CheckReturnValue
    override fun hasWebDriverStarted(): Boolean {
        return webDriver != null
    }

    @get:CheckReturnValue
    override val getAndCheckWebDriver: WebDriver
        get() {
            if (webDriver == null || !browserHealthChecker.isBrowserStillOpen(webDriver)) {
                log.info("Webdriver has been closed meanwhile")
                close()
                throw IllegalStateException("Webdriver has been closed meanwhile")
            }
            return webDriver
        }

    @CheckReturnValue
    override fun browserDownloadsFolder(): DownloadsFolder? {
        return browserDownloadsFolder
    }

    /**
     * Close the webdriver.
     *
     *
     * NB! The behaviour was changed in Selenide 5.4.0
     * Even if webdriver was created by user - it will be closed.
     * It may hurt if you try to use this browser after closing.
     */
    override fun close() {
        closeDriverCommand.close(config, webDriver, proxy)
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebDriverWrapper::class.java)
    }

    init {
        Objects.requireNonNull(config, "config must not be null")
        Objects.requireNonNull(webDriver, "webDriver must not be null")
        this.config = config
        this.webDriver = webDriver
        proxy = selenideProxy
        this.browserDownloadsFolder = browserDownloadsFolder
        this.browserHealthChecker = browserHealthChecker
        this.closeDriverCommand = closeDriverCommand
    }
}

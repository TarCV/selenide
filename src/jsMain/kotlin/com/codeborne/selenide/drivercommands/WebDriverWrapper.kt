package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.Driver
import com.codeborne.selenide.drivercommands.WebDriverWrapper
import okio.ExperimentalFileSystem
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory

/**
 * A `Driver` implementation which uses given webdriver [and proxy].
 * It doesn't open a new browser.
 * It doesn't start a new proxy.
 */
@ExperimentalFileSystem
class WebDriverWrapper private constructor(
    config: Config, webDriver: WebDriver,
    selenideProxy: /*SelenideProxyServer*/Nothing?, browserDownloadsFolder: DownloadsFolder,
    browserHealthChecker: BrowserHealthChecker, closeDriverCommand: CloseDriverCommand
) : Driver {
    private val config: Config
    override val webDriver: WebDriver
    /*override val proxy: SelenideProxyServerNothing?*/
    private val browserDownloadsFolder: DownloadsFolder
    private val browserHealthChecker: BrowserHealthChecker
    private val closeDriverCommand: CloseDriverCommand

    constructor(
        config: Config, webDriver: WebDriver,
        selenideProxy: /*SelenideProxyServer*/Nothing?, browserDownloadsFolder: DownloadsFolder
    ) : this(config, webDriver, selenideProxy, browserDownloadsFolder, BrowserHealthChecker(), CloseDriverCommand()) {
    }
    override fun config(): Config {
        return config
    }
    override fun browser(): Browser {
        return Browser(config.browser(), config.headless())
    }
    override fun hasWebDriverStarted(): Boolean {
        return webDriver != null
    }
    override val getAndCheckWebDriver: WebDriver
        get() {
            if (webDriver == null || !browserHealthChecker.isBrowserStillOpen(webDriver)) {
                log.info("Webdriver has been closed meanwhile")
                close()
                throw IllegalStateException("Webdriver has been closed meanwhile")
            }
            return webDriver
        }
    override fun browserDownloadsFolder(): DownloadsFolder {
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
        closeDriverCommand.close(config, webDriver, null /* TODO: proxy*/)
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebDriverWrapper::class)
    }

    init {
        checkNotNull(config) { "config must not be null" }
        checkNotNull(webDriver) { "webDriver must not be null" }
        this.config = config
        this.webDriver = webDriver
        /* TODO: proxy = selenideProxy*/
        this.browserDownloadsFolder = browserDownloadsFolder
        this.browserHealthChecker = browserHealthChecker
        this.closeDriverCommand = closeDriverCommand
    }
}

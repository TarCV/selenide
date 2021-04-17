package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.BrowserDownloadsFolder.Companion.from
import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.impl.FileHelper
import com.codeborne.selenide.impl.FileNamer
import com.codeborne.selenide.proxy.SelenideProxyServer
import com.codeborne.selenide.webdriver.WebDriverFactory
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.EventFiringWebDriver
import org.openqa.selenium.support.events.WebDriverEventListener
import org.slf4j.LoggerFactory
import java.io.File
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class CreateDriverCommand internal constructor(private val fileNamer: FileNamer) {
    constructor() : this(FileNamer()) {}

    fun createDriver(
        config: Config,
        factory: WebDriverFactory,
        userProvidedProxy: Proxy?,
        listeners: List<WebDriverEventListener>
    ): Result {
        check(config.reopenBrowserOnFail()) {
            "No webdriver is bound to current thread: " + Thread.currentThread().id +
                    ", and cannot create a new webdriver because reopenBrowserOnFail=false"
        }
        var selenideProxyServer: SelenideProxyServer? = null
        var browserProxy = userProvidedProxy
        if (config.proxyEnabled()) {
            try {
                selenideProxyServer = SelenideProxyServer(config, userProvidedProxy)
                selenideProxyServer.start()
                browserProxy = selenideProxyServer.createSeleniumProxy()
            } catch (e: NoClassDefFoundError) {
                throw IllegalStateException(
                    "Cannot initialize proxy. " +
                            "Probably you should add BrowserUpProxy dependency to your project.", e
                )
            }
        }
        val browserDownloadsFolder = if (config.remote() != null) null else FileHelper.ensureFolderExists(
            File(
                config.downloadsFolder(),
                fileNamer.generateFileName()
            ).absoluteFile
        )
        val webdriver = factory.createWebDriver(config, browserProxy, browserDownloadsFolder)
        log.info(
            "Create webdriver in current thread {}: {} -> {}",
            Thread.currentThread().id, webdriver.javaClass.simpleName, webdriver
        )
        val webDriver = addListeners(webdriver, listeners)
        Runtime.getRuntime().addShutdownHook(
            Thread(SelenideDriverFinalCleanupThread(config, webDriver, selenideProxyServer))
        )
        if (browserDownloadsFolder != null) {
            Runtime.getRuntime().addShutdownHook(
                Thread { FileHelper.deleteFolderIfEmpty(browserDownloadsFolder) }
            )
        }
        return Result(webDriver, selenideProxyServer, from(browserDownloadsFolder))
    }

    private fun addListeners(webdriver: WebDriver, listeners: List<WebDriverEventListener>): WebDriver {
        if (listeners.isEmpty()) {
            return webdriver
        }
        val wrapper = EventFiringWebDriver(webdriver)
        for (listener in listeners) {
            log.info("Add listener to webdriver: {}", listener)
            wrapper.register(listener)
        }
        return wrapper
    }

    @ParametersAreNonnullByDefault
    class Result(
        val webDriver: WebDriver,
        val selenideProxyServer: SelenideProxyServer?,
        val browserDownloadsFolder: DownloadsFolder?
    )

    companion object {
        private val log = LoggerFactory.getLogger(CreateDriverCommand::class.java)
    }
}

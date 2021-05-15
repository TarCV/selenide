package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.BrowserDownloadsFolder.Companion.from
import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.impl.FileHelper
import com.codeborne.selenide.impl.FileNamer
import com.codeborne.selenide.webdriver.WebDriverFactory
import okio.ExperimentalFileSystem
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.events.EventFiringWebDriver
import org.openqa.selenium.support.events.WebDriverEventListener
import org.slf4j.LoggerFactory
import java.io.File
import javax.annotation.ParametersAreNonnullByDefault
import kotlin.concurrent.thread

@ParametersAreNonnullByDefault
class CreateDriverCommand internal constructor(private val fileNamer: FileNamer) {
    constructor() : this(FileNamer()) {}

    @ExperimentalFileSystem
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
        val browserProxy = userProvidedProxy
        val browserDownloadsFolder = if (config.remote() != null) null else FileHelper.ensureFolderExists(
            FileHelper.pathOf(
                config.downloadsFolder(),
                fileNamer.generateFileName()
            )
        )
        val webdriver = factory.createWebDriver(config, browserProxy, browserDownloadsFolder)
        log.info(
            "Create webdriver in current thread {}: {} -> {}",
            Thread.currentThread().id, webdriver.javaClass.simpleName, webdriver
        )
        val webDriver = addListeners(webdriver, listeners)
        Runtime.getRuntime().addShutdownHook(
            thread {
                SelenideDriverFinalCleanupThread(config, webDriver, null).invoke()
            }
        )
        if (browserDownloadsFolder != null) {
            Runtime.getRuntime().addShutdownHook(
                Thread { FileHelper.deleteFolderIfEmpty(browserDownloadsFolder) }
            )
        }
        return Result(
            webDriver,
            null,
            browserDownloadsFolder?.let { from(it) }
        )
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

    @ExperimentalFileSystem
    @ParametersAreNonnullByDefault
    class Result(
        val webDriver: WebDriver,
        val selenideProxyServer: Nothing?,
        val browserDownloadsFolder: DownloadsFolder?
    )

    companion object {
        private val log = LoggerFactory.getLogger(CreateDriverCommand::class.java)
    }
}

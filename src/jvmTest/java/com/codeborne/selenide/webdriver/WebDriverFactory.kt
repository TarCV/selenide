package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Browsers
import com.codeborne.selenide.Config
import com.codeborne.selenide.SelenideDriver
import okio.ExperimentalFileSystem
import okio.Path
import org.openqa.selenium.BuildInfo
import org.openqa.selenium.HasCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.UnsupportedCommandException
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.TimeUnit
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class WebDriverFactory {
    private val factories = factories()
    private val remoteDriverFactory = RemoteDriverFactory()
    private val browserResizer = BrowserResizer()
    @CheckReturnValue
    private fun factories(): Map<String, Class<out AbstractDriverFactory>> {
        val result: MutableMap<String, Class<out AbstractDriverFactory>> = HashMap()
        result[Browsers.CHROME] = ChromeDriverFactory::class.java
        return result
    }

    @ExperimentalFileSystem
    @CheckReturnValue
    fun createWebDriver(config: Config, proxy: Proxy?, browserDownloadsFolder: Path?): WebDriver {
        log.debug("browser={}", config.browser())
        log.debug("browser.version={}", config.browserVersion())
        log.debug("remote={}", config.remote())
        log.debug("browserSize={}", config.browserSize())
        log.debug("startMaximized={}", config.startMaximized())
        if (browserDownloadsFolder != null) {
            log.debug("downloadsFolder={}", browserDownloadsFolder.toFile().absolutePath)
        }
        val browser = Browser(config.browser(), config.headless())
        val webdriver = createWebDriverInstance(config, browser, proxy, browserDownloadsFolder)
        browserResizer.adjustBrowserSize(config, webdriver)
        browserResizer.adjustBrowserPosition(config, webdriver)
        setLoadTimeout(config, webdriver)
        logBrowserVersion(webdriver)
        log.info("Selenide v. {}", SelenideDriver::class.java.getPackage().implementationVersion)
        logSeleniumInfo()
        return webdriver
    }

    private fun setLoadTimeout(config: Config, webdriver: WebDriver) {
        try {
            webdriver.manage().timeouts().pageLoadTimeout(config.pageLoadTimeout(), TimeUnit.MILLISECONDS)
        } catch (e: UnsupportedCommandException) {
            log.info("Failed to set page load timeout to {} ms: {}", config.pageLoadTimeout(), e.toString())
        } catch (e: RuntimeException) {
            log.error("Failed to set page load timeout to {} ms", config.pageLoadTimeout(), e)
        }
    }

    @ExperimentalFileSystem
    @CheckReturnValue
    private fun createWebDriverInstance(
        config: Config, browser: Browser,
        proxy: Proxy?,
        browserDownloadsFolder: Path?
    ): WebDriver {
        val webdriverFactory = findFactory(browser)
        return if (config.remote() != null) {
            val capabilities = webdriverFactory.createCapabilities(config, browser, proxy, browserDownloadsFolder)
            remoteDriverFactory.create(config, capabilities)
        } else {
            if (config.driverManagerEnabled()) {
                webdriverFactory.setupWebdriverBinary()
            }
            webdriverFactory.create(config, browser, proxy, browserDownloadsFolder)
        }
    }

    @CheckReturnValue
    private fun findFactory(browser: Browser): DriverFactory {
        val factoryClass = factories[browser.name.toLowerCase()] ?: DefaultDriverFactory::class.java
        return try {
            factoryClass.getConstructor().newInstance()
        } catch (e: InstantiationException) {
            throw RuntimeException("Failed to initialize " + factoryClass.name, e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Failed to initialize " + factoryClass.name, e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Failed to initialize " + factoryClass.name, e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Failed to initialize " + factoryClass.name, e)
        }
    }

    private fun logSeleniumInfo() {
        val seleniumInfo = BuildInfo()
        log.info("Selenium WebDriver v. {} build time: {}", seleniumInfo.releaseLabel, seleniumInfo.buildTime)
    }

    private fun logBrowserVersion(webdriver: WebDriver) {
        if (webdriver is HasCapabilities) {
            val capabilities = (webdriver as HasCapabilities).capabilities
            log.info(
                "BrowserName={} Version={} Platform={}",
                capabilities.browserName, capabilities.version, capabilities.platform
            )
        } else {
            log.info("BrowserName={}", webdriver.javaClass.name)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebDriverFactory::class.java)
    }
}

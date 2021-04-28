package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import org.openqa.selenium.HasCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.UnsupportedCommandException
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory
import support.reflect.IllegalAccessException
import support.reflect.InstantiationException
import support.reflect.InvocationTargetException
import support.reflect.NoSuchMethodException
import support.reflect.newInstance
import kotlin.time.milliseconds

class WebDriverFactory {
    private val factories = factories()
    private val remoteDriverFactory = RemoteDriverFactory()
// TODO:   private val browserResizer = BrowserResizer()
    private fun factories(): Map<String, kotlin.reflect.KClass<out AbstractDriverFactory>> {
        val result: MutableMap<String, kotlin.reflect.KClass<out AbstractDriverFactory>> = HashMap()
// TODO:        result[Browsers.CHROME] = ChromeDriverFactory::class
        return result
    }
    @kotlin.time.ExperimentalTime
    fun createWebDriver(config: Config, proxy: Proxy?, browserDownloadsFolder: Path?): WebDriver {
        log.debug("browser={}", config.browser())
        log.debug("browser.version={}", config.browserVersion())
        log.debug("remote={}", config.remote())
        log.debug("browserSize={}", config.browserSize())
        log.debug("startMaximized={}", config.startMaximized())
        if (browserDownloadsFolder != null) {
            log.debug("downloadsFolder={}", browserDownloadsFolder.absolutePath)
        }
        val browser = Browser(config.browser(), config.headless())
        val webdriver = createWebDriverInstance(config, browser, proxy, browserDownloadsFolder)
/*
TODO:        browserResizer.adjustBrowserSize(config, webdriver)
        browserResizer.adjustBrowserPosition(config, webdriver)
*/
        setLoadTimeout(config, webdriver)
        logBrowserVersion(webdriver)
// TODO:       log.info("Selenide v. {}", SelenideDriver::class.getPackage().implementationVersion)
// TODO:       logSeleniumInfo()
        return webdriver
    }

    @kotlin.time.ExperimentalTime
    private fun setLoadTimeout(config: Config, webdriver: WebDriver) {
        try {
            webdriver.manage().timeouts().pageLoadTimeout(config.pageLoadTimeout().milliseconds)
        } catch (e: UnsupportedCommandException) {
            log.info("Failed to set page load timeout to {} ms: {}", config.pageLoadTimeout(), e.toString())
        } catch (e: RuntimeException) {
            log.error("Failed to set page load timeout to {} ms", config.pageLoadTimeout(), e)
        }
    }
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
    private fun findFactory(browser: Browser): DriverFactory {
        val factoryClass = factories[browser.name.toLowerCase()] ?: DefaultDriverFactory::class
        return try {
            factoryClass.newInstance()
        } catch (e: InstantiationException) {
            throw RuntimeException("Failed to initialize " + factoryClass.simpleName, e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Failed to initialize " + factoryClass.simpleName, e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Failed to initialize " + factoryClass.simpleName, e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Failed to initialize " + factoryClass.simpleName, e)
        }
    }

/*
TODO:    private fun logSeleniumInfo() {
        val seleniumInfo = BuildInfo()
        log.info("Selenium WebDriver v. {} build time: {}", seleniumInfo.releaseLabel, seleniumInfo.buildTime)
    }
*/

    private fun logBrowserVersion(webdriver: WebDriver) {
        if (webdriver is HasCapabilities) {
            val capabilities = (webdriver as HasCapabilities).capabilities
            log.info(
                "BrowserName={} Version={} Platform={}",
                capabilities.browserName, capabilities.version, capabilities.platform
            )
        } else {
            log.info("BrowserName={}", webdriver::class.simpleName)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebDriverFactory::class)
    }
}


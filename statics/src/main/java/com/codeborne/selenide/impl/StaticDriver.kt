package com.codeborne.selenide.impl

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.Driver
import com.codeborne.selenide.WebDriverRunner
import com.codeborne.selenide.proxy.SelenideProxyServer
import org.openqa.selenium.WebDriver
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

/**
 * A `Driver` implementation which uses thread-local
 * webdriver and proxy from `WebDriverRunner`.
 *
 * @see WebDriverRunner
 *
 * @see StaticConfig
 */
@ParametersAreNonnullByDefault
class StaticDriver : Driver {
    private val config: Config = StaticConfig()
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
        return WebDriverRunner.hasWebDriverStarted()
    }

    override val webDriver: WebDriver
        get() {
            return WebDriverRunner.webDriver
        }

    override val proxy: SelenideProxyServer?
      get() {
          return WebDriverRunner.selenideProxy
      }

      override val getAndCheckWebDriver: WebDriver
        get() {
            return WebDriverRunner.getAndCheckWebDriver
        }

    @CheckReturnValue
    override fun browserDownloadsFolder(): DownloadsFolder {
        return WebDriverRunner.browserDownloadsFolder
    }

    override fun close() {
        WebDriverRunner.closeWebDriver()
    }
}

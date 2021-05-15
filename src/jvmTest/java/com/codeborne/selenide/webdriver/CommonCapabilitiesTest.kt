package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.SelenideConfig
import okio.ExperimentalFileSystem
import okio.Path
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.Capabilities
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import javax.annotation.CheckReturnValue
import javax.annotation.Nonnull

@ExperimentalFileSystem
internal class CommonCapabilitiesTest : WithAssertions {
    private val driverFactory: AbstractDriverFactory = DummyDriverFactory()
    private val proxy = Mockito.mock(Proxy::class.java)
    @Test
    fun transferCapabilitiesFromConfiguration() {
        val config = SelenideConfig()
        config.pageLoadStrategy("foo")
        val commonCapabilities: Capabilities = driverFactory.createCommonCapabilities(config, browser(config), proxy)
        assertThat(asBool(commonCapabilities.getCapability(CapabilityType.ACCEPT_INSECURE_CERTS))).isTrue
        assertThat(asBool(commonCapabilities.getCapability(CapabilityType.ACCEPT_SSL_CERTS))).isTrue
        assertThat(commonCapabilities.getCapability(CapabilityType.PAGE_LOAD_STRATEGY)).isEqualTo(config.pageLoadStrategy())
    }

    private fun asBool(raw: Any?): Boolean {
        if (raw != null) {
            if (raw is String) {
                return java.lang.Boolean.parseBoolean(raw as String?)
            } else if (raw is Boolean) {
                return raw
            }
        }
        return false
    }

    private fun browser(config: SelenideConfig): Browser {
        return Browser(config.browser(), config.headless())
    }

    @ExperimentalFileSystem
    private class DummyDriverFactory : AbstractDriverFactory() {
        @CheckReturnValue
        @Nonnull
        fun create(config: Config, browser: Browser, proxy: Proxy?, browserDownloadsFolder: Path?): WebDriver {
            return Mockito.mock(WebDriver::class.java)
        }

        override fun createCapabilities(
            config: Config,
            browser: Browser,
            proxy: Any?,
            browserDownloadsFolder: Path?
        ): MutableCapabilities {
            return DesiredCapabilities()
        }

        @ExperimentalFileSystem
        override fun create(config: Config, browser: Browser, proxy: Any?, browserDownloadsFolder: Path?): WebDriver {
            return Mockito.mock(WebDriver::class.java)
        }

        override fun setupWebdriverBinary() {
            // no op
        }
    }
}

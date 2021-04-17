package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.Config
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.proxy.SelenideProxyServer
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.UnreachableBrowserException
import org.slf4j.LoggerFactory
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class CloseDriverCommand {
    fun close(config: Config, webDriver: WebDriver?, selenideProxyServer: SelenideProxyServer?) {
        val threadId = Thread.currentThread().id
        if (config.holdBrowserOpen()) {
            log.info("Hold browser and proxy open: {} -> {}, {}", threadId, webDriver, selenideProxyServer)
            return
        }
        if (webDriver != null) {
            val start = System.currentTimeMillis()
            log.info("Close webdriver: {} -> {}...", threadId, webDriver)
            close(webDriver)
            log.info("Closed webdriver {} in {} ms", threadId, System.currentTimeMillis() - start)
        }
        if (selenideProxyServer != null) {
            val start = System.currentTimeMillis()
            log.info("Close proxy server: {} -> {}...", threadId, selenideProxyServer)
            selenideProxyServer.shutdown()
            log.info("Closed proxy server {} in {} ms", threadId, System.currentTimeMillis() - start)
        }
    }

    private fun close(webdriver: WebDriver) {
        try {
            webdriver.quit()
        } catch (e: UnreachableBrowserException) {
            // It happens for Firefox. It's ok: browser is already closed.
            log.debug("Browser is unreachable", e)
        } catch (cannotCloseBrowser: WebDriverException) {
            log.error("Cannot close browser: {}", Cleanup.of.webdriverExceptionMessage(cannotCloseBrowser))
        } catch (e: RuntimeException) {
            log.error("Cannot close browser", e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CloseDriverCommand::class.java)
    }
}

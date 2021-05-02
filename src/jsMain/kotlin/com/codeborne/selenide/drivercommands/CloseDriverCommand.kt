package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.Config
import com.codeborne.selenide.impl.Cleanup
import org.lighthousegames.logging.logging
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.UnreachableBrowserException
import support.System

class CloseDriverCommand {
    fun close(config: Config, webDriver: WebDriver?, selenideProxyServer: /* TODO: SelenideProxyServer*/Nothing?) {
        val threadId = support.System.currentThreadId()
        if (config.holdBrowserOpen()) {
            log.info { "Hold browser and proxy open: $threadId -> $webDriver, $selenideProxyServer"  }
            return
        }
        if (webDriver != null) {
            val start = System.currentTimeMillis()
            log.info { "Close webdriver: $threadId -> ${webDriver}..." }
            close(webDriver)
            log.info { "Closed webdriver $threadId in ${System.currentTimeMillis() - start} ms" }
        }
        if (selenideProxyServer != null) {
            val start = System.currentTimeMillis()
            log.info { "Close proxy server: $threadId -> ${selenideProxyServer}..." }
// TODO:            selenideProxyServer.shutdown()
            log.info { "Closed proxy server $threadId in ${System.currentTimeMillis() - start} ms" }
        }
    }

    private fun close(webdriver: WebDriver) {
        try {
            webdriver.quit()
        } catch (e: UnreachableBrowserException) {
            // It happens for Firefox. It's ok: browser is already closed.
            // TODO: was debug in Java
            log.warn(e) {"Browser is unreachable"}
        } catch (cannotCloseBrowser: WebDriverException) {
            log.error { "Cannot close browser: ${Cleanup.of.webdriverExceptionMessage(cannotCloseBrowser)}" }
        } catch (e: RuntimeException) {
            log.error(e) {"Cannot close browser"}
        }
    }

    companion object {
        private val log = logging(CloseDriverCommand::class.simpleName)
    }
}

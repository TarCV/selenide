package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.Config
import com.codeborne.selenide.impl.Cleanup
import org.lighthousegames.logging.logging
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.remote.UnreachableBrowserException
import support.System

class CloseDriverCommand {
    fun close(config: Config, webDriver: org.openqa.selenium.WebDriver?, selenideProxyServer: /* TODO: SelenideProxyServer*/Nothing?) {
        if (config.holdBrowserOpen()) {
            log.info { "Hold browser and proxy open:  -> $webDriver, $selenideProxyServer"  }
            return
        }
        if (webDriver != null) {
            val start = System.currentTimeMillis()
            log.info { "Close webdriver:  -> ${webDriver}..." }
            close(webDriver)
            log.info { "Closed webdriver  in ${System.currentTimeMillis() - start} ms" }
        }
        if (selenideProxyServer != null) {
            val start = System.currentTimeMillis()
            log.info { "Close proxy server:  -> ${selenideProxyServer}..." }
// TODO:            selenideProxyServer.shutdown()
            log.info { "Closed proxy server  in ${System.currentTimeMillis() - start} ms" }
        }
    }

    private fun close(webdriver: org.openqa.selenium.WebDriver) {
        try {
            webdriver.quit()
        } catch (e: org.openqa.selenium.remote.UnreachableBrowserException) {
            // It happens for Firefox. It's ok: browser is already closed.
            // TODO: was debug in Java
            log.warn(e) {"Browser is unreachable"}
        } catch (cannotCloseBrowser: org.openqa.selenium.WebDriverException) {
            log.error { "Cannot close browser: ${Cleanup.of.webdriverExceptionMessage(cannotCloseBrowser)}" }
        } catch (e: RuntimeException) {
            log.error(e) {"Cannot close browser"}
        }
    }

    companion object {
        private val log = logging(CloseDriverCommand::class.simpleName)
    }
}

package com.codeborne.selenide.drivercommands

import org.openqa.selenium.NoSuchSessionException
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.UnreachableBrowserException
import org.slf4j.LoggerFactory
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class BrowserHealthChecker {
    fun isBrowserStillOpen(webDriver: WebDriver): Boolean {
        return try {
            webDriver.title
            true
        } catch (e: UnreachableBrowserException) {
            log.debug("Browser is unreachable", e)
            false
        } catch (e: NoSuchWindowException) {
            log.debug("Browser window is not found", e)
            false
        } catch (e: NoSuchSessionException) {
            log.debug("Browser session is not found", e)
            false
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BrowserHealthChecker::class.java)
    }
}

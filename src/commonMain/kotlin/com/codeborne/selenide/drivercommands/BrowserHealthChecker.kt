package com.codeborne.selenide.drivercommands

import org.lighthousegames.logging.logging
import org.openqa.selenium.NoSuchSessionException
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.UnreachableBrowserException

class BrowserHealthChecker {
    fun isBrowserStillOpen(webDriver: org.openqa.selenium.WebDriver): Boolean {
        return try {
            webDriver.title
            true
        } catch (e: org.openqa.selenium.remote.UnreachableBrowserException) {
            // TODO: was debug in Java
            log.warn(e) { "Browser is unreachable" }
            false
        } catch (e: org.openqa.selenium.NoSuchWindowException) {
            // TODO: was debug in Java
            log.warn(e) {"Browser window is not found" }
            false
        } catch (e: org.openqa.selenium.NoSuchSessionException) {
            // TODO: was debug in Java
            log.warn(e) {"Browser session is not found" }
            false
        }
    }

    companion object {
        private val log = logging(BrowserHealthChecker::class.simpleName)
    }
}

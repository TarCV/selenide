package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Config
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.LocalFileDetector
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.MalformedURLException
import java.net.URL
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class RemoteDriverFactory {
    fun create(config: Config, capabilities: MutableCapabilities?): WebDriver {
        return try {
            val webDriver = RemoteWebDriver(URL(config.remote()), capabilities)
            webDriver.fileDetector = LocalFileDetector()
            webDriver
        } catch (e: MalformedURLException) {
            throw IllegalArgumentException("Invalid 'remote' parameter: " + config.remote(), e)
        }
    }
}

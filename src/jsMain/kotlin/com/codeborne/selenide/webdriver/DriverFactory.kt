package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import java.io.Path

interface DriverFactory {
    fun setupWebdriverBinary()
    fun createCapabilities(
        config: Config, browser: Browser,
        proxy: Proxy?, browserDownloadsFolder: Path?
    ): MutableCapabilities
    fun create(config: Config, browser: Browser, proxy: Proxy?, browserDownloadsFolder: Path?): WebDriver
}

package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import java.io.File

interface DriverFactory {
    fun setupWebdriverBinary()
    fun createCapabilities(
        config: Config, browser: Browser,
        proxy: Proxy?, browserDownloadsFolder: File?
    ): MutableCapabilities
    fun create(config: Config, browser: Browser, proxy: Proxy?, browserDownloadsFolder: File?): WebDriver
}

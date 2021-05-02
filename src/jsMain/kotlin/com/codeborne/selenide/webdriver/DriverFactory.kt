package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import okio.ExperimentalFileSystem
import okio.Path
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver

interface DriverFactory {
    fun setupWebdriverBinary()

    @ExperimentalFileSystem
    fun createCapabilities(
        config: Config, browser: Browser,
        proxy: Proxy?, browserDownloadsFolder: Path?
    ): MutableCapabilities

    @ExperimentalFileSystem
    fun create(config: Config, browser: Browser, proxy: Proxy?, browserDownloadsFolder: Path?): WebDriver
}

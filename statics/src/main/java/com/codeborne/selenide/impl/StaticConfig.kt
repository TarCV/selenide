package com.codeborne.selenide.impl

import com.codeborne.selenide.AssertionMode
import com.codeborne.selenide.Config
import com.codeborne.selenide.Configuration.assertionMode
import com.codeborne.selenide.Configuration.baseUrl
import com.codeborne.selenide.Configuration.browser
import com.codeborne.selenide.Configuration.browserBinary
import com.codeborne.selenide.Configuration.browserCapabilities
import com.codeborne.selenide.Configuration.browserPosition
import com.codeborne.selenide.Configuration.browserSize
import com.codeborne.selenide.Configuration.browserVersion
import com.codeborne.selenide.Configuration.clickViaJs
import com.codeborne.selenide.Configuration.downloadsFolder
import com.codeborne.selenide.Configuration.driverManagerEnabled
import com.codeborne.selenide.Configuration.fastSetValue
import com.codeborne.selenide.Configuration.fileDownload
import com.codeborne.selenide.Configuration.headless
import com.codeborne.selenide.Configuration.holdBrowserOpen
import com.codeborne.selenide.Configuration.pageLoadStrategy
import com.codeborne.selenide.Configuration.pageLoadTimeout
import com.codeborne.selenide.Configuration.pollingInterval
import com.codeborne.selenide.Configuration.proxyEnabled
import com.codeborne.selenide.Configuration.proxyHost
import com.codeborne.selenide.Configuration.proxyPort
import com.codeborne.selenide.Configuration.remote
import com.codeborne.selenide.Configuration.reopenBrowserOnFail
import com.codeborne.selenide.Configuration.reportsFolder
import com.codeborne.selenide.Configuration.reportsUrl
import com.codeborne.selenide.Configuration.savePageSource
import com.codeborne.selenide.Configuration.screenshots
import com.codeborne.selenide.Configuration.selectorMode
import com.codeborne.selenide.Configuration.startMaximized
import com.codeborne.selenide.Configuration.timeout
import com.codeborne.selenide.Configuration.versatileSetValue
import com.codeborne.selenide.Configuration.webdriverLogsEnabled
import com.codeborne.selenide.FileDownloadMode
import com.codeborne.selenide.SelectorMode
import org.openqa.selenium.MutableCapabilities

/**
 * A non-static facade for static fields in [com.codeborne.selenide.Configuration]
 *
 * It was created only to keep backward compatibility in Selenide 5.0.0: every time when somebody modifies, say,
 * [com.codeborne.selenide.Configuration.timeout], it will immediately reflect in [StaticConfig.timeout]
 *
 * This class should not be normally used in end user's code.
 */
class StaticConfig : Config {
    override fun baseUrl(): String {
        return baseUrl
    }

    override fun timeout(): Long {
        return timeout
    }

    override fun pollingInterval(): Long {
        return pollingInterval
    }

    override fun holdBrowserOpen(): Boolean {
        return holdBrowserOpen
    }

    override fun reopenBrowserOnFail(): Boolean {
        return reopenBrowserOnFail
    }

    override fun clickViaJs(): Boolean {
        return clickViaJs
    }

    override fun screenshots(): Boolean {
        return screenshots
    }

    override fun savePageSource(): Boolean {
        return savePageSource
    }

    override fun reportsFolder(): String {
        return reportsFolder
    }

    override fun downloadsFolder(): String {
        return downloadsFolder
    }

    override fun reportsUrl(): String {
        return reportsUrl!!
    }

    override fun fastSetValue(): Boolean {
        return fastSetValue
    }

    override fun versatileSetValue(): Boolean {
        return versatileSetValue
    }

    override fun selectorMode(): SelectorMode {
        return selectorMode
    }

    override fun assertionMode(): AssertionMode {
        return assertionMode
    }

    override fun fileDownload(): FileDownloadMode {
        return fileDownload
    }

    override fun proxyEnabled(): Boolean {
        return proxyEnabled
    }

    override fun proxyHost(): String {
        return proxyHost
    }

    override fun proxyPort(): Int {
        return proxyPort
    }

    override fun browser(): String {
        return browser
    }

    override fun headless(): Boolean {
        return headless
    }

    override fun remote(): String {
        return remote!!
    }

    override fun browserSize(): String {
        return browserSize
    }

    override fun browserVersion(): String {
        return browserVersion!!
    }

    override fun browserPosition(): String {
        return browserPosition!!
    }

    override fun startMaximized(): Boolean {
        return startMaximized
    }

    override fun driverManagerEnabled(): Boolean {
        return driverManagerEnabled
    }

    override fun webdriverLogsEnabled(): Boolean {
        return webdriverLogsEnabled
    }

    override fun browserBinary(): String {
        return browserBinary
    }

    override fun pageLoadStrategy(): String {
        return pageLoadStrategy
    }

    override fun pageLoadTimeout(): Long {
        return pageLoadTimeout
    }

    override fun browserCapabilities(): MutableCapabilities {
        return browserCapabilities
    }
}

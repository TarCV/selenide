package com.codeborne.selenide

import com.codeborne.selenide.impl.CiReportUrl
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.remote.DesiredCapabilities

class SelenideConfig : Config {
    private var browser = System.getProperty("selenide.browser", Browsers.CHROME)
    private var headless = java.lang.Boolean.parseBoolean(System.getProperty("selenide.headless", "false"))
    private var remote = System.getProperty("selenide.remote")
    private var browserSize = System.getProperty("selenide.browserSize", "1366x768")
    private var browserVersion = System.getProperty("selenide.browserVersion")
    private var browserPosition = System.getProperty("selenide.browserPosition")
    private var startMaximized = java.lang.Boolean.parseBoolean(System.getProperty("selenide.startMaximized", "false"))
    private var driverManagerEnabled =
        java.lang.Boolean.parseBoolean(System.getProperty("selenide.driverManagerEnabled", "true"))
    private var webdriverLogsEnabled =
        java.lang.Boolean.parseBoolean(System.getProperty("selenide.webdriverLogsEnabled", "false"))
    private var browserBinary = System.getProperty("selenide.browserBinary", "")
    private var pageLoadStrategy = System.getProperty("selenide.pageLoadStrategy", "normal")
    private var pageLoadTimeout = System.getProperty("selenide.pageLoadTimeout", "30000").toLong()
    private var browserCapabilities: MutableCapabilities = DesiredCapabilities()
    private var baseUrl = System.getProperty("selenide.baseUrl", "http://localhost:8080")
    private var timeout = System.getProperty("selenide.timeout", "4000").toLong()
    private var pollingInterval = System.getProperty("selenide.pollingInterval", "200").toLong()
    private var holdBrowserOpen = java.lang.Boolean.getBoolean("selenide.holdBrowserOpen")
    private var reopenBrowserOnFail =
        java.lang.Boolean.parseBoolean(System.getProperty("selenide.reopenBrowserOnFail", "true"))
    private var clickViaJs = java.lang.Boolean.parseBoolean(System.getProperty("selenide.clickViaJs", "false"))
    private var screenshots = java.lang.Boolean.parseBoolean(System.getProperty("selenide.screenshots", "true"))
    private var savePageSource = java.lang.Boolean.parseBoolean(System.getProperty("selenide.savePageSource", "true"))
    private var reportsFolder = System.getProperty("selenide.reportsFolder", "build/reports/tests")
    private var downloadsFolder = System.getProperty("selenide.downloadsFolder", "build/downloads")
    private var reportsUrl = CiReportUrl().getReportsUrl(System.getProperty("selenide.reportsUrl"))
    private var fastSetValue = java.lang.Boolean.parseBoolean(System.getProperty("selenide.fastSetValue", "false"))
    private var versatileSetValue =
        java.lang.Boolean.parseBoolean(System.getProperty("selenide.versatileSetValue", "false"))
    private var selectorMode = SelectorMode.valueOf(System.getProperty("selenide.selectorMode", SelectorMode.CSS.name))
    private var assertionMode =
        AssertionMode.valueOf(System.getProperty("selenide.assertionMode", AssertionMode.STRICT.name))
    private var fileDownload =
        FileDownloadMode.valueOf(System.getProperty("selenide.fileDownload", FileDownloadMode.HTTPGET.name))
    private var proxyEnabled = java.lang.Boolean.parseBoolean(System.getProperty("selenide.proxyEnabled", "false"))
    private var proxyHost = System.getProperty("selenide.proxyHost", "")
    private var proxyPort = System.getProperty("selenide.proxyPort", "0").toInt()
    override fun baseUrl(): String {
        return baseUrl
    }

    fun baseUrl(baseUrl: String): SelenideConfig {
        this.baseUrl = baseUrl
        return this
    }

    override fun timeout(): Long {
        return timeout
    }

    fun timeout(timeout: Long): SelenideConfig {
        this.timeout = timeout
        return this
    }

    override fun pollingInterval(): Long {
        return pollingInterval
    }

    fun pollingInterval(pollingInterval: Long): SelenideConfig {
        this.pollingInterval = pollingInterval
        return this
    }

    override fun holdBrowserOpen(): Boolean {
        return holdBrowserOpen
    }

    fun holdBrowserOpen(holdBrowserOpen: Boolean): SelenideConfig {
        this.holdBrowserOpen = holdBrowserOpen
        return this
    }

    override fun reopenBrowserOnFail(): Boolean {
        return reopenBrowserOnFail
    }

    fun reopenBrowserOnFail(reopenBrowserOnFail: Boolean): SelenideConfig {
        this.reopenBrowserOnFail = reopenBrowserOnFail
        return this
    }

    override fun clickViaJs(): Boolean {
        return clickViaJs
    }

    fun clickViaJs(clickViaJs: Boolean): SelenideConfig {
        this.clickViaJs = clickViaJs
        return this
    }

    override fun screenshots(): Boolean {
        return screenshots
    }

    fun screenshots(screenshots: Boolean): SelenideConfig {
        this.screenshots = screenshots
        return this
    }

    override fun savePageSource(): Boolean {
        return savePageSource
    }

    fun savePageSource(savePageSource: Boolean): SelenideConfig {
        this.savePageSource = savePageSource
        return this
    }

    override fun reportsFolder(): String {
        return reportsFolder
    }

    fun reportsFolder(reportsFolder: String): SelenideConfig {
        this.reportsFolder = reportsFolder
        return this
    }

    override fun downloadsFolder(): String {
        return downloadsFolder
    }

    fun downloadsFolder(downloadsFolder: String): SelenideConfig {
        this.downloadsFolder = downloadsFolder
        return this
    }

    override fun reportsUrl(): String {
        return reportsUrl!!
    }

    fun reportsUrl(reportsUrl: String?): SelenideConfig {
        this.reportsUrl = reportsUrl
        return this
    }

    override fun fastSetValue(): Boolean {
        return fastSetValue
    }

    fun fastSetValue(fastSetValue: Boolean): SelenideConfig {
        this.fastSetValue = fastSetValue
        return this
    }

    override fun versatileSetValue(): Boolean {
        return versatileSetValue
    }

    fun versatileSetValue(versatileSetValue: Boolean): SelenideConfig {
        this.versatileSetValue = versatileSetValue
        return this
    }

    override fun selectorMode(): SelectorMode {
        return selectorMode
    }

    fun selectorMode(selectorMode: SelectorMode): SelenideConfig {
        this.selectorMode = selectorMode
        return this
    }

    override fun assertionMode(): AssertionMode {
        return assertionMode
    }

    fun assertionMode(assertionMode: AssertionMode): SelenideConfig {
        this.assertionMode = assertionMode
        return this
    }

    override fun fileDownload(): FileDownloadMode {
        return fileDownload
    }

    fun fileDownload(fileDownload: FileDownloadMode): SelenideConfig {
        this.fileDownload = fileDownload
        return this
    }

    override fun proxyEnabled(): Boolean {
        return proxyEnabled
    }

    fun proxyEnabled(proxyEnabled: Boolean): SelenideConfig {
        this.proxyEnabled = proxyEnabled
        return this
    }

    override fun proxyHost(): String {
        return proxyHost
    }

    fun proxyHost(proxyHost: String): SelenideConfig {
        this.proxyHost = proxyHost
        return this
    }

    override fun proxyPort(): Int {
        return proxyPort
    }

    fun proxyPort(proxyPort: Int): SelenideConfig {
        this.proxyPort = proxyPort
        return this
    }

    override fun browser(): String {
        return browser
    }

    fun browser(browser: String): SelenideConfig {
        this.browser = browser
        return this
    }

    override fun headless(): Boolean {
        return headless
    }

    fun headless(headless: Boolean): SelenideConfig {
        this.headless = headless
        return this
    }

    override fun remote(): String {
        return remote
    }

    fun remote(remote: String): SelenideConfig {
        this.remote = remote
        return this
    }

    override fun browserSize(): String {
        return browserSize
    }

    fun browserSize(browserSize: String): SelenideConfig {
        this.browserSize = browserSize
        return this
    }

    override fun browserVersion(): String {
        return browserVersion
    }

    fun browserVersion(browserVersion: String): SelenideConfig {
        this.browserVersion = browserVersion
        return this
    }

    override fun browserPosition(): String {
        return browserPosition
    }

    fun browserPosition(browserPosition: String): SelenideConfig {
        this.browserPosition = browserPosition
        return this
    }

    override fun startMaximized(): Boolean {
        return startMaximized
    }

    fun startMaximized(startMaximized: Boolean): SelenideConfig {
        this.startMaximized = startMaximized
        return this
    }

    override fun driverManagerEnabled(): Boolean {
        return driverManagerEnabled
    }

    fun driverManagerEnabled(driverManagerEnabled: Boolean): SelenideConfig {
        this.driverManagerEnabled = driverManagerEnabled
        return this
    }

    override fun webdriverLogsEnabled(): Boolean {
        return webdriverLogsEnabled
    }

    fun webdriverLogsEnabled(webdriverLogsEnabled: Boolean): SelenideConfig {
        this.webdriverLogsEnabled = webdriverLogsEnabled
        return this
    }

    override fun browserBinary(): String {
        return browserBinary
    }

    fun browserBinary(browserBinary: String): SelenideConfig {
        this.browserBinary = browserBinary
        return this
    }

    override fun pageLoadStrategy(): String {
        return pageLoadStrategy
    }

    override fun pageLoadTimeout(): Long {
        return pageLoadTimeout
    }

    fun pageLoadStrategy(pageLoadStrategy: String): SelenideConfig {
        this.pageLoadStrategy = pageLoadStrategy
        return this
    }

    fun pageLoadTimeout(pageLoadTimeout: Long): SelenideConfig {
        this.pageLoadTimeout = pageLoadTimeout
        return this
    }

    override fun browserCapabilities(): MutableCapabilities {
        return browserCapabilities
    }

    fun browserCapabilities(browserCapabilities: DesiredCapabilities): SelenideConfig {
        this.browserCapabilities = browserCapabilities
        return this
    }
}

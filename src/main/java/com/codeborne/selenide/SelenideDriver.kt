package com.codeborne.selenide

import com.codeborne.selenide.drivercommands.LazyDriver
import com.codeborne.selenide.drivercommands.Navigator
import com.codeborne.selenide.drivercommands.WebDriverWrapper
import com.codeborne.selenide.files.FileFilters
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest
import com.codeborne.selenide.impl.ElementFinder
import com.codeborne.selenide.impl.PageObjectFactory
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.ScreenShotLaboratory
import com.codeborne.selenide.impl.WebElementWrapper
import com.codeborne.selenide.logevents.SelenideLogger
import com.codeborne.selenide.proxy.SelenideProxyServer
import org.openqa.selenium.By
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.events.WebDriverEventListener
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

/**
 * "Selenide driver" is a container for WebDriver + proxy server + settings
 */
@ParametersAreNonnullByDefault
open class SelenideDriver {
    private val config: Config
    private val driver: Driver

    @JvmOverloads
    constructor(config: Config, listeners: List<WebDriverEventListener> = emptyList()) : this(
        config,
        LazyDriver(config, null, listeners)
    ) {
    }

    constructor(config: Config, driver: Driver) {
        this.config = config
        this.driver = driver
    }

    @JvmOverloads
    constructor(
        config: Config, webDriver: WebDriver, selenideProxy: SelenideProxyServer?,
        browserDownloadsFolder: DownloadsFolder = SharedDownloadsFolder(config.downloadsFolder())
    ) {
        this.config = config
        driver = WebDriverWrapper(config, webDriver, selenideProxy, browserDownloadsFolder)
    }

    @CheckReturnValue
    fun config(): Config {
        return config
    }

    @CheckReturnValue
    fun driver(): Driver {
        return driver
    }

    fun open() {
        navigator.open(this)
    }

    fun open(relativeOrAbsoluteUrl: String) {
        navigator.open(this, relativeOrAbsoluteUrl)
    }

    fun open(absoluteUrl: URL) {
        navigator.open(this, absoluteUrl)
    }

    fun open(relativeOrAbsoluteUrl: String, domain: String, login: String, password: String) {
        navigator.open(this, relativeOrAbsoluteUrl, domain, login, password)
    }

    fun open(relativeOrAbsoluteUrl: String, authenticationType: AuthenticationType, credentials: Credentials) {
        navigator.open(this, relativeOrAbsoluteUrl, authenticationType, credentials)
    }

    fun open(absoluteUrl: URL, domain: String, login: String, password: String) {
        navigator.open(this, absoluteUrl, domain, login, password)
    }

    @CheckReturnValue
    fun <PageObjectClass> open(
        relativeOrAbsoluteUrl: String,
        pageObjectClassClass: Class<PageObjectClass>
    ): PageObjectClass {
        return open(relativeOrAbsoluteUrl, "", "", "", pageObjectClassClass)
    }

    @CheckReturnValue
    fun <PageObjectClass> open(
        absoluteUrl: URL,
        pageObjectClassClass: Class<PageObjectClass>
    ): PageObjectClass {
        return open(absoluteUrl, "", "", "", pageObjectClassClass)
    }

    @CheckReturnValue
    fun <PageObjectClass> open(
        relativeOrAbsoluteUrl: String,
        domain: String, login: String, password: String,
        pageObjectClassClass: Class<PageObjectClass>
    ): PageObjectClass {
        open(relativeOrAbsoluteUrl, domain, login, password)
        return page(pageObjectClassClass)
    }

    @CheckReturnValue
    fun <PageObjectClass> open(
        absoluteUrl: URL, domain: String, login: String, password: String,
        pageObjectClassClass: Class<PageObjectClass>
    ): PageObjectClass {
        open(absoluteUrl, domain, login, password)
        return page(pageObjectClassClass)
    }

    @CheckReturnValue
    fun <PageObjectClass> page(pageObjectClass: Class<PageObjectClass>): PageObjectClass {
        return pageFactory.page(driver(), pageObjectClass)
    }

    @CheckReturnValue
    fun <PageObjectClass, T : PageObjectClass> page(pageObject: T): PageObjectClass {
        return pageFactory.page(driver(), pageObject)
    }

    fun refresh() {
        navigator.refresh(driver())
    }

    fun back() {
        navigator.back(driver())
    }

    fun forward() {
        navigator.forward(driver())
    }

    fun updateHash(hash: String) {
        SelenideLogger.run("updateHash", hash) {
            val localHash = if (hash[0] == '#') hash.substring(1) else hash
            executeJavaScript<Any>("window.location.hash='$localHash'")
        }
    }

    @CheckReturnValue
    fun browser(): Browser {
        return driver().browser()
    }

    @get:CheckReturnValue
    // TODO: why this was NonNullable in Java code?
    val proxy: SelenideProxyServer?
        get() = driver().proxy

    fun hasWebDriverStarted(): Boolean {
        return driver().hasWebDriverStarted()
    }

    @get:CheckReturnValue
    val webDriver: WebDriver
        get() = driver.webDriver

    val andCheckWebDriver: WebDriver
        get() = driver.getAndCheckWebDriver

    fun clearCookies() {
        SelenideLogger.run("clearCookies", "") { driver().clearCookies() }
    }

    fun close() {
        driver.close()
    }

    fun <T> executeJavaScript(jsCode: String, vararg arguments: Any): T {
        return driver().executeJavaScript(jsCode, *arguments)
    }

    fun <T> executeAsyncJavaScript(jsCode: String, vararg arguments: Any): T {
        return driver().executeAsyncJavaScript(jsCode, *arguments)
    }

    @get:CheckReturnValue
    val focusedElement: WebElement?
        get() = executeJavaScript<WebElement>("return document.activeElement")

    @CheckReturnValue
    fun Wait(): SelenideWait {
        return SelenideWait(webDriver, config().timeout(), config().pollingInterval())
    }

    fun zoom(factor: Double) {
        executeJavaScript<Any>(
            "document.body.style.transform = 'scale(' + arguments[0] + ')';" +
                    "document.body.style.transformOrigin = '0 0';",
            factor
        )
    }

    fun title(): String? {
        return webDriver.title
    }

    @CheckReturnValue
    fun `$`(webElement: WebElement): SelenideElement {
        return WebElementWrapper.wrap(driver(), webElement)
    }

    @CheckReturnValue
    fun `$`(cssSelector: String): SelenideElement {
        return find(cssSelector)
    }

    @CheckReturnValue
    fun find(cssSelector: String): SelenideElement {
        return find(By.cssSelector(cssSelector))
    }

    @CheckReturnValue
    fun `$x`(xpathExpression: String): SelenideElement {
        return find(By.xpath(xpathExpression))
    }

    @CheckReturnValue
    fun `$`(seleniumSelector: By): SelenideElement {
        return find(seleniumSelector)
    }

    @CheckReturnValue
    fun `$`(seleniumSelector: By, index: Int): SelenideElement {
        return find(seleniumSelector, index)
    }

    @CheckReturnValue
    fun `$`(cssSelector: String, index: Int): SelenideElement {
        return ElementFinder.wrap(driver(), cssSelector, index)
    }

    @CheckReturnValue
    fun find(criteria: By): SelenideElement {
        return ElementFinder.wrap(driver(), null, criteria, 0)
    }

    @CheckReturnValue
    fun find(criteria: By, index: Int): SelenideElement {
        return ElementFinder.wrap(driver(), null, criteria, index)
    }

    @CheckReturnValue
    fun `$$`(elements: Collection<WebElement>): ElementsCollection {
        return ElementsCollection(driver(), elements)
    }

    @CheckReturnValue
    fun `$$`(cssSelector: String): ElementsCollection {
        return ElementsCollection(driver(), cssSelector)
    }

    @CheckReturnValue
    fun `$$x`(xpathExpression: String): ElementsCollection {
        return `$$`(By.xpath(xpathExpression))
    }

    @CheckReturnValue
    fun findAll(seleniumSelector: By): ElementsCollection {
        return ElementsCollection(driver(), seleniumSelector)
    }

    @CheckReturnValue
    fun findAll(cssSelector: String): ElementsCollection {
        return ElementsCollection(driver(), By.cssSelector(cssSelector))
    }

    @CheckReturnValue
    fun `$$`(criteria: By): ElementsCollection {
        return findAll(criteria)
    }

    @CheckReturnValue
    fun getSelectedRadio(radioField: By): SelenideElement? {
        for (radio in `$$`(radioField)) {
            if (radio.getAttribute("checked") != null) {
                return `$`(radio)
            }
        }
        return null
    }

    @CheckReturnValue
    fun modal(): Modal {
        return Modal(driver())
    }

    @get:CheckReturnValue
    val webDriverLogs: WebDriverLogs
        get() = WebDriverLogs(driver())

    fun clearBrowserLocalStorage() {
        executeJavaScript<Any>("localStorage.clear();")
    }

    fun atBottom(): Boolean {
        return executeJavaScript("return window.pageYOffset + window.innerHeight >= document.body.scrollHeight")
    }

    fun switchTo(): SelenideTargetLocator {
        return driver().switchTo()
    }

    @CheckReturnValue
    fun url(): String {
        return webDriver.currentUrl
    }

    @CheckReturnValue
    fun source(): String? {
        return webDriver.pageSource
    }

    @get:CheckReturnValue
    val currentFrameUrl: String
        get() = executeJavaScript<Any>("return window.location.href").toString()

    @get:CheckReturnValue
    val userAgent: String
        get() = driver().userAgent

    /**
     * Take a screenshot of the current page
     *
     * @return absolute path of the screenshot taken or null if failed to create screenshot
     * @since 5.14.0
     */
    @CheckReturnValue
    fun screenshot(fileName: String): String? {
        return screenshots.takeScreenShot(driver(), fileName)
    }

    /**
     * Take a screenshot of the current page
     *
     * @return The screenshot (as bytes, base64 or temporary file)
     * @since 5.14.0
     */
    @CheckReturnValue
    fun <T> screenshot(outputType: OutputType<T>): T? {
        return screenshots.takeScreenShot(driver(), outputType)
    }

    @Throws(IOException::class, URISyntaxException::class)
    fun download(url: String): File {
        return download(URI(url), config.timeout())
    }

    @Throws(IOException::class, URISyntaxException::class)
    fun download(url: String, timeoutMs: Long): File {
        return download(URI(url), timeoutMs)
    }

    @Throws(IOException::class)
    fun download(url: URI): File {
        return download(url, config.timeout())
    }

    @Throws(IOException::class)
    fun download(url: URI, timeoutMs: Long): File {
        return downloadFileWithHttpRequest()!!.download(driver(), url, timeoutMs, FileFilters.none())
    }

    @get:CheckReturnValue
    val localStorage: LocalStorage
        get() = LocalStorage(driver())

    @get:CheckReturnValue
    val sessionStorage: SessionStorage
        get() = SessionStorage(driver())

    @get:CheckReturnValue
    val clipboard: Clipboard
        get() = Plugins.inject(ClipboardService::class.java).getClipboard(driver())

    companion object {
        private val navigator = Navigator()
        private val screenshots = ScreenShotLaboratory.getInstance()
        private val pageFactory = Plugins.inject(
            PageObjectFactory::class.java
        )
        private var downloadFileWithHttpRequest: DownloadFileWithHttpRequest? = null
        @Synchronized
        private fun downloadFileWithHttpRequest(): DownloadFileWithHttpRequest? {
            if (downloadFileWithHttpRequest == null) downloadFileWithHttpRequest = DownloadFileWithHttpRequest()
            return downloadFileWithHttpRequest
        }
    }
}

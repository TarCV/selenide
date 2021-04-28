package com.codeborne.selenide

import com.codeborne.selenide.drivercommands.LazyDriver
import com.codeborne.selenide.drivercommands.Navigator
import com.codeborne.selenide.impl.ElementFinder
import com.codeborne.selenide.impl.PageObjectFactory
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.WebElementWrapper
import com.codeborne.selenide.logevents.SelenideLogger
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.events.WebDriverEventListener
import support.URL

/**
 * "Selenide driver" is a container for WebDriver + proxy server + settings
 */
open class SelenideDriver {
    private val config: Config
    private val driver: Driver

    constructor(config: Config, listeners: List<WebDriverEventListener> = emptyList()) : this(
        config,
        LazyDriver(config, null, listeners)
    ) {
    }

    constructor(config: Config, driver: Driver) {
        this.config = config
        this.driver = driver
    }

/*
TODO:    constructor(
        config: Config, webDriver: WebDriver, selenideProxy: SelenideProxyServer?,
        browserDownloadsFolder: DownloadsFolder = SharedDownloadsFolder(config.downloadsFolder())
    ) {
        this.config = config
        driver = WebDriverWrapper(config, webDriver, selenideProxy, browserDownloadsFolder)
    }
*/
    fun config(): Config {
        return config
    }
    fun driver(): Driver {
        return driver
    }

    fun open() {
        navigator.open(this)
    }

    suspend fun open(relativeOrAbsoluteUrl: String) {
        navigator.open(this, relativeOrAbsoluteUrl)
    }

    suspend fun open(absoluteUrl: URL) {
        navigator.open(this, absoluteUrl)
    }

    suspend fun open(relativeOrAbsoluteUrl: String, domain: String, login: String, password: String) {
        navigator.open(this, relativeOrAbsoluteUrl, domain, login, password)
    }

/* TODO:
    fun open(relativeOrAbsoluteUrl: String, authenticationType: AuthenticationType, credentials: Credentials) {
        navigator.open(this, relativeOrAbsoluteUrl, authenticationType, credentials)
    }
*/

    suspend fun open(absoluteUrl: URL, domain: String, login: String, password: String) {
        navigator.open(this, absoluteUrl, domain, login, password)
    }
    suspend fun <PageObjectClass: Any> open(
        relativeOrAbsoluteUrl: String,
        pageObjectClassClass: kotlin.reflect.KClass<PageObjectClass>
    ): PageObjectClass {
        return open(relativeOrAbsoluteUrl, "", "", "", pageObjectClassClass)
    }
    suspend fun <PageObjectClass: Any> open(
        absoluteUrl: URL,
        pageObjectClassClass: kotlin.reflect.KClass<PageObjectClass>
    ): PageObjectClass {
        return open(absoluteUrl, "", "", "", pageObjectClassClass)
    }
    suspend fun <PageObjectClass: Any> open(
        relativeOrAbsoluteUrl: String,
        domain: String, login: String, password: String,
        pageObjectClassClass: kotlin.reflect.KClass<PageObjectClass>
    ): PageObjectClass {
        open(relativeOrAbsoluteUrl, domain, login, password)
        return page(pageObjectClassClass)
    }
    suspend fun <PageObjectClass: Any> open(
        absoluteUrl: URL, domain: String, login: String, password: String,
        pageObjectClassClass: kotlin.reflect.KClass<PageObjectClass>
    ): PageObjectClass {
        open(absoluteUrl, domain, login, password)
        return page(pageObjectClassClass)
    }
    fun <PageObjectClass: Any> page(pageObjectClass: kotlin.reflect.KClass<PageObjectClass>): PageObjectClass {
        return pageFactory.page(driver(), pageObjectClass)
    }
    fun <PageObjectClass: Any, T : PageObjectClass> page(pageObject: T): PageObjectClass {
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
    fun browser(): Browser {
        return driver().browser()
    }
// TODO: why this was NonNullable in Java code?
/*
 TODO:    val proxy: SelenideProxyServer?
        get() = driver().proxy
*/

    fun hasWebDriverStarted(): Boolean {
        return driver().hasWebDriverStarted()
    }
    val webDriver: WebDriver
        get() = driver.webDriver

    val andCheckWebDriver: WebDriver
        get() = driver.getAndCheckWebDriver

    suspend fun clearCookies() {
        SelenideLogger.runAsync("clearCookies", "") { driver().clearCookies() }
    }

    fun close() {
        driver.close()
    }

    fun <T> executeJavaScript(jsCode: String, vararg arguments: Any): T? {
        return driver().executeJavaScript(jsCode, *arguments)
    }

    fun <T> executeAsyncJavaScript(jsCode: String, vararg arguments: Any): T? {
        return driver().executeAsyncJavaScript(jsCode, *arguments)
    }
    val focusedElement: WebElement?
        get() = executeJavaScript<WebElement>("return document.activeElement")

    @kotlin.time.ExperimentalTime
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
    fun `$`(webElement: WebElement): SelenideElement {
        return WebElementWrapper.wrap(driver(), webElement)
    }
    fun `$`(cssSelector: String): SelenideElement {
        return find(cssSelector)
    }
    fun find(cssSelector: String): SelenideElement {
        return find(By.cssSelector(cssSelector))
    }
    fun `$x`(xpathExpression: String): SelenideElement {
        return find(By.xpath(xpathExpression))
    }
    fun `$`(seleniumSelector: By): SelenideElement {
        return find(seleniumSelector)
    }
    fun `$`(seleniumSelector: By, index: Int): SelenideElement {
        return find(seleniumSelector, index)
    }
    fun `$`(cssSelector: String, index: Int): SelenideElement {
        return ElementFinder.wrap(driver(), cssSelector, index)
    }
    fun find(criteria: By): SelenideElement {
        return ElementFinder.wrap(driver(), null, criteria, 0)
    }
    fun find(criteria: By, index: Int): SelenideElement {
        return ElementFinder.wrap(driver(), null, criteria, index)
    }
    fun `$$`(elements: Collection<WebElement>): ElementsCollection {
        return ElementsCollection(driver(), elements)
    }
    fun `$$`(cssSelector: String): ElementsCollection {
        return ElementsCollection(driver(), cssSelector)
    }
    fun `$$x`(xpathExpression: String): ElementsCollection {
        return `$$`(By.xpath(xpathExpression))
    }
    fun findAll(seleniumSelector: By): ElementsCollection {
        return ElementsCollection(driver(), seleniumSelector)
    }
    fun findAll(cssSelector: String): ElementsCollection {
        return ElementsCollection(driver(), By.cssSelector(cssSelector))
    }
    fun `$$`(criteria: By): ElementsCollection {
        return findAll(criteria)
    }
    suspend fun getSelectedRadio(radioField: By): SelenideElement? {
        for (radio in `$$`(radioField)) {
            if (radio.getAttribute("checked") != null) {
                return `$`(radio)
            }
        }
        return null
    }
    fun modal(): Modal {
        return Modal(driver())
    }
    val webDriverLogs: WebDriverLogs
        get() = WebDriverLogs(driver())

    fun clearBrowserLocalStorage() {
        executeJavaScript<Any>("localStorage.clear();")
    }

    fun atBottom(): Boolean {
        return executeJavaScript("return window.pageYOffset + window.innerHeight >= document.body.scrollHeight")
          ?: false // TODO: was unsafe in java code
    }

    fun switchTo(): SelenideTargetLocator {
        return driver().switchTo()
    }
    fun url(): String {
        return webDriver.currentUrl
    }
    fun source(): String? {
        return webDriver.pageSource
    }
    val currentFrameUrl: String
        get() = executeJavaScript<Any>("return window.location.href").toString()
    val userAgent: String
        get() = driver().userAgent

    /**
     * Take a screenshot of the current page
     *
     * @return absolute path of the screenshot taken or null if failed to create screenshot
     * @since 5.14.0
     */
/* TODO:    fun screenshot(fileName: String): String? {
        return screenshots.takeScreenShot(driver(), fileName)
    }

    *//**
     * Take a screenshot of the current page
     *
     * @return The screenshot (as bytes, base64 or temporary file)
     * @since 5.14.0
     *//*
    fun <T: Any> screenshot(outputType: OutputType<T>): T? {
        return screenshots.takeScreenShot(driver(), outputType)
    }*/

/* TODO:    fun download(url: String): Path {
        return download(URI(url), config.timeout())
    }

    fun download(url: String, timeoutMs: Long): Path {
        return download(URI(url), timeoutMs)
    }

    fun download(url: URI): Path {
        return download(url, config.timeout())
    }


    fun download(url: URI, timeoutMs: Long): Path {
        return downloadFileWithHttpRequest.download(driver(), url, timeoutMs, FileFilters.none())
    }
*/
    val localStorage: LocalStorage
        get() = LocalStorage(driver())
    val sessionStorage: SessionStorage
        get() = SessionStorage(driver())
/* TODO:
    val clipboard: Clipboard
        get() = Plugins.inject(ClipboardService::class).getClipboard(driver())
*/

    companion object {
        private val navigator = Navigator()
// TODO:        private val screenshots = ScreenShotLaboratory.instance
        private val pageFactory = Plugins.inject(
            PageObjectFactory::class
        )
        /* TODO: val downloadFileWithHttpRequest: DownloadFileWithHttpRequest by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            DownloadFileWithHttpRequest()
        }*/
    }
}

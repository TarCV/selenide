package com.codeborne.selenide

import org.openqa.selenium.By
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.logging.Level
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault
import kotlinx.coroutines.delay
import okio.Path
/**
 * The main starting point of Selenide.
 *
 * You start with methods [.open] for opening the tested application page and
 * [.$] for searching web elements.
 */
@ParametersAreNonnullByDefault
object Selenide {
    /**
     * The main starting point in your tests.
     * Open a browser window with given URL.
     *
     * If browser window was already opened before, it will be reused.
     *
     * Don't bother about closing the browser - it will be closed automatically when all your tests are done.
     *
     * @param relativeOrAbsoluteUrl
     * If not starting with "http://" or "https://" or "file://", it's considered to be relative URL.
     * In this case, it's prepended by baseUrl
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun open(relativeOrAbsoluteUrl: String) {
        WebDriverRunner.selenideDriver.open(relativeOrAbsoluteUrl)
    }

    /**
     * @see Selenide.open
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun open(absoluteUrl: URL) {
        WebDriverRunner.selenideDriver.open(absoluteUrl)
    }

    /**
     * The main starting point in your tests.
     *
     *
     * Open a browser window with given URL and credentials for basic authentication
     *
     *
     * If browser window was already opened before, it will be reused.
     *
     *
     * Don't bother about closing the browser - it will be closed automatically when all your tests are done.
     *
     *
     * If not starting with "http://" or "https://" or "file://", it's considered to be relative URL.
     *
     *
     * In this case, it's prepended by baseUrl
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun open(relativeOrAbsoluteUrl: String, domain: String, login: String, password: String) {
        WebDriverRunner.selenideDriver.open(relativeOrAbsoluteUrl, domain, login, password)
    }

    /**
     * The main starting point in your tests.
     *
     *
     * Open browser and pass authentication using build-in proxy.
     *
     *
     * A common authenticationType is "Basic". See Web HTTP reference for other types.
     *
     *
     * This method can only work if - `Configuration.fileDownload == Configuration.FileDownloadMode.PROXY;`
     *
     * @see [Web HTTP reference](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Proxy-Authorization)
     *
     * @see AuthenticationType
     */
/* TODO:
    @JvmStatic
    suspend fun open(
        relativeOrAbsoluteUrl: String,
        authenticationType: AuthenticationType,
        login: String,
        password: String
    ) {
        val credentials = Credentials(login, password)
        open(relativeOrAbsoluteUrl, authenticationType, credentials)
    }
*/

    /**
     * The main starting point in your tests.
     *
     *
     * Open browser and pass authentication using build-in proxy.
     *
     *
     * A common authenticationType is "Basic". See Web HTTP reference for other types.
     *
     *
     * This method can only work if - `Configuration.fileDownload == Configuration.FileDownloadMode.PROXY;`
     *
     * @see [Web HTTP reference](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Proxy-Authorization)
     *
     * @see AuthenticationType
     *
     * @see Credentials
     */
    /* TODO: @JvmStatic
    suspend fun open(relativeOrAbsoluteUrl: String, authenticationType: AuthenticationType, credentials: Credentials) {
        WebDriverRunner.selenideDriver.open(relativeOrAbsoluteUrl, authenticationType, credentials)
    }*/

    /**
     * @see Selenide.open
     */
    /* TODO: @JvmStatic
    suspend fun open(absoluteUrl: URL, domain: String, login: String, password: String) {
        WebDriverRunner.selenideDriver.open(absoluteUrl, domain, login, password)
    }*/

    /**
     * Open an empty browser (without opening any pages).
     * E.g. useful for starting mobile applications in Appium.
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun open() {
        WebDriverRunner.selenideDriver.open()
    }

    @JvmStatic
    inline fun using(webDriver: WebDriver, noinline lambda: () -> Unit) {
        WebDriverRunner.using(webDriver, lambda)
    }

    /**
     * Update the hash of the window location.
     * Useful to navigate in ajax apps without reloading the page, since open(url) makes a full page reload.
     *
     * @param hash value for window.location.hash - Accept either "#hash" or "hash".
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun updateHash(hash: String) {
        WebDriverRunner.selenideDriver.updateHash(hash)
    }

    /**
     * Open a web page and create PageObject for it.
     * @return PageObject of given class
     */
    /* TODO: @CheckReturnValue
    @JvmStatic
    suspend fun <PageObjectClass: Any> open(
        relativeOrAbsoluteUrl: String,
        pageObjectClassClass: Class<PageObjectClass>
    ): PageObjectClass {
        return WebDriverRunner.selenideDriver.open(relativeOrAbsoluteUrl, pageObjectClassClass)
    }

    *//**
     * Open a web page and create PageObject for it.
     * @return PageObject of given class
     *//*
    @CheckReturnValue
    @JvmStatic
    suspend fun <PageObjectClass: Any> open(
        absoluteUrl: URL,
        pageObjectClassClass: Class<PageObjectClass>
    ): PageObjectClass {
        return WebDriverRunner.selenideDriver.open(absoluteUrl, pageObjectClassClass)
    }

    *//**
     * Open a web page using Basic Auth credentials and create PageObject for it.
     * @return PageObject of given class
     *//*
    @CheckReturnValue
    @JvmStatic
    suspend fun<PageObjectClass: Any> open(
        relativeOrAbsoluteUrl: String,
        domain: String, login: String, password: String,
        pageObjectClassClass: Class<PageObjectClass>
    ): PageObjectClass {
        return WebDriverRunner.selenideDriver
            .open(relativeOrAbsoluteUrl, domain, login, password, pageObjectClassClass)
    }

    *//**
     * Open a web page using Basic Auth credentials and create PageObject for it.
     * @return PageObject of given class
     *//*
    @CheckReturnValue
    @JvmStatic
    suspend fun<PageObjectClass: Any> open(
        absoluteUrl: URL, domain: String, login: String, password: String,
        pageObjectClassClass: Class<PageObjectClass>
    ): PageObjectClass {
        return WebDriverRunner.selenideDriver.open(absoluteUrl, domain, login, password, pageObjectClassClass)
    }*/

    /**
     * Close the current window, quitting the browser if it's the last window currently open.
     *
     * @see WebDriver.close
     */
    @JvmStatic
    suspend fun closeWindow() {
        WebDriverRunner.closeWindow()
    }

    /**
     *
     * Close the browser if it's open.
     * <br></br>
     *
     * NB! Method quits this driver, closing every associated window.
     *
     * @see WebDriver.quit
     */
    @JvmStatic
    suspend fun closeWebDriver() {
        WebDriverRunner.closeWebDriver()
    }

    @Deprecated("Use either {@link #closeWindow()} or {@link #closeWebDriver()}")
    @JvmStatic
    suspend fun close() {
        closeWebDriver()
    }

    /**
     * Reload current page
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun refresh() {
        WebDriverRunner.selenideDriver.refresh()
    }

    /**
     * Navigate browser back to previous page
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun back() {
        WebDriverRunner.selenideDriver.back()
    }

    /**
     * Navigate browser forward to next page
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun forward() {
        WebDriverRunner.selenideDriver.forward()
    }

    /**
     *
     * @return title of the page
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun title(): String? {
        return WebDriverRunner.selenideDriver.title()
    }

    /**
     * Not recommended. Test should not sleep, but should wait for some condition instead.
     * @param milliseconds Time to sleep in milliseconds
     */
    @JvmStatic
    suspend fun sleep(milliseconds: Long) {
        delay(milliseconds)
    }

    /**
     * Take the screenshot of current page and save to file fileName.html and fileName.png
     * @param fileName Name of file (without extension) to save HTML and PNG to
     * @return The name of resulting file
     */
/*
TODO:    @CheckReturnValue
    @JvmStatic
    fun screenshot(fileName: String): String? {
        return WebDriverRunner.selenideDriver.screenshot(fileName)
    }
*/

    /**
     * Take the screenshot of current page and return it.
     * @param outputType type of the returned screenshot
     * @return The screenshot (as bytes, base64 or temporary file)
     * or null if webdriver does not support taking screenshots.
     */
/*    @CheckReturnValue
TODO:    @JvmStatic
    fun <T: Any> screenshot(outputType: OutputType<T>): T? {
        return WebDriverRunner.selenideDriver.screenshot(outputType)
    }*/

    /**
     * Wrap standard Selenium WebElement into SelenideElement
     * to use additional methods like shouldHave(), selectOption() etc.
     *
     * @param webElement standard Selenium WebElement
     * @return given WebElement wrapped into SelenideElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$`(webElement: WebElement): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(webElement)
    }

    /**
     * Locates the first element matching given CSS selector
     * ATTENTION! This method doesn't start any search yet!
     * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
     * @return SelenideElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$`(cssSelector: String): SelenideElement {
        return WebDriverRunner.selenideDriver.find(cssSelector)
    }

    /**
     * Locates the first element matching given XPATH expression
     * ATTENTION! This method doesn't start any search yet!
     * @param xpathExpression any XPATH expression // *[@id='value'] //E[contains(@A, 'value')]
     * @return SelenideElement which locates elements via XPath
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$x`(xpathExpression: String): SelenideElement {
        return WebDriverRunner.selenideDriver.`$x`(xpathExpression)
    }

    /**
     * Locates the first element matching given CSS selector
     * ATTENTION! This method doesn't start any search yet!
     * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
     * @return SelenideElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$`(seleniumSelector: By): SelenideElement {
        return WebDriverRunner.selenideDriver.find(seleniumSelector)
    }

    /**
     * @see .getElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$`(seleniumSelector: By, index: Int): SelenideElement {
        return WebDriverRunner.selenideDriver.find(seleniumSelector, index)
    }

    /**
     * @see .$
     * @param parent the WebElement to search elements in
     * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
     * @return SelenideElement
     */
    @Deprecated(
        """please use $(parent).$(String) which is the same
    (method will not be removed until 4.x or later)
    """
    )
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$`(parent: WebElement, cssSelector: String): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(parent).find(cssSelector)
    }

    /**
     * Locates the Nth element matching given criteria
     * ATTENTION! This method doesn't start any search yet!
     * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
     * @param index 0..N
     * @return SelenideElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$`(cssSelector: String, index: Int): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(cssSelector, index)
    }

    /**
     * @see .$
     * @param parent the WebElement to search elements in
     * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
     * @param index 0..N
     * @return SelenideElement
     */
    @CheckReturnValue
    @Deprecated(
        """please use $(parent).$(String, int) which is the same
    (method will not be removed until 4.x or later)
    """
    )
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$`(parent: WebElement, cssSelector: String, index: Int): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(parent).find(cssSelector, index)
    }

    /**
     * @see .$
     * @param parent the WebElement to search elements in
     * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
     * @return SelenideElement
     */
    @CheckReturnValue
    @Deprecated(
        """please use $(parent).$(By) which is the same
    (method will not be removed until 4.x or later)
    """
    )
    // TODO: why this hadn't Nullable in Java code?
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$`(parent: WebElement, seleniumSelector: By): SelenideElement? {
        return WebDriverRunner.selenideDriver.`$`(parent).find(seleniumSelector)
    }

    /**
     * @see .$
     * @param parent the WebElement to search elements in
     * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
     * @param index 0..N
     * @return SelenideElement
     */
    @CheckReturnValue
    @Deprecated(
        """please use $(parent).$(By, int) which is the same
    (method will not be removed until 4.x or later)
    """
    )
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$`(parent: WebElement, seleniumSelector: By, index: Int): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(parent).find(seleniumSelector, index)
    }

    /**
     * Initialize collection with Elements
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$$`(elements: Collection<WebElement>): ElementsCollection {
        return WebDriverRunner.selenideDriver.`$$`(elements)
    }

    /**
     * Locates all elements matching given CSS selector.
     * ATTENTION! This method doesn't start any search yet!
     * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
     * and at the same time is implementation of WebElement interface,
     * meaning that you can call methods .sendKeys(), click() etc. on it.
     *
     * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
     * @return empty list if element was no found
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$$`(cssSelector: String): ElementsCollection {
        return WebDriverRunner.selenideDriver.`$$`(cssSelector)
    }

    /**
     * Locates all elements matching given XPATH expression.
     * ATTENTION! This method doesn't start any search yet!
     * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
     * and at the same time is implementation of WebElement interface,
     * meaning that you can call methods .sendKeys(), click() etc. on it.
     * @param xpathExpression any XPATH expression // *[@id='value'] //E[contains(@A, 'value')]
     * @return ElementsCollection which locates elements via XPath
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$$x`(xpathExpression: String): ElementsCollection {
        return WebDriverRunner.selenideDriver.`$$x`(xpathExpression)
    }

    /**
     * Locates all elements matching given CSS selector.
     * ATTENTION! This method doesn't start any search yet!
     * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
     * and at the same time is implementation of WebElement interface,
     * meaning that you can call methods .sendKeys(), click() etc. on it.
     *
     * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
     * @return empty list if element was no found
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$$`(seleniumSelector: By): ElementsCollection {
        return WebDriverRunner.selenideDriver.`$$`(seleniumSelector)
    }

    /**
     * @see .$$
     * @param parent the WebElement to search elements in
     * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
     * @return empty list if element was no found
     */
    @CheckReturnValue
    @Deprecated(
        """please use $(parent).${"$"}$(String) which is the same
    (method will not be removed until 4.x or later)
    """
    )
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$$`(parent: WebElement, cssSelector: String): ElementsCollection {
        return WebDriverRunner.selenideDriver.`$`(parent).findAll(cssSelector)
    }

    /**
     * @see .$$
     * @see `Selenide.$$`
     */
    @CheckReturnValue
    @Deprecated(
        """please use $(parent).${"$"}$(By) which is the same
    (method will not be removed until 4.x or later)
    """
    )
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun `$$`(parent: WebElement, seleniumSelector: By): ElementsCollection {
        return WebDriverRunner.selenideDriver.`$`(parent).findAll(seleniumSelector)
    }

    /**
     * Wrap standard Selenium WebElement into SelenideElement
     * to use additional methods like shouldHave(), selectOption() etc.
     *
     * @param webElement standard Selenium WebElement
     * @return given WebElement wrapped into SelenideElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun element(webElement: WebElement): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(webElement)
    }

    /**
     * Locates the first element matching given CSS selector
     * ATTENTION! This method doesn't start any search yet!
     * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
     * @return SelenideElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun element(cssSelector: String): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(cssSelector)
    }

    /**
     * Locates the first element matching given CSS selector
     * ATTENTION! This method doesn't start any search yet!
     * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
     * @return SelenideElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun element(seleniumSelector: By): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(seleniumSelector)
    }

    /**
     * Locates the Nth element matching given criteria
     * ATTENTION! This method doesn't start any search yet!
     * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
     * @param index 0..N
     * @return SelenideElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun element(seleniumSelector: By, index: Int): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(seleniumSelector, index)
    }

    /**
     * Locates the Nth element matching given criteria
     * ATTENTION! This method doesn't start any search yet!
     * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
     * @param index 0..N
     * @return SelenideElement
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun element(cssSelector: String, index: Int): SelenideElement {
        return WebDriverRunner.selenideDriver.`$`(cssSelector, index)
    }

    /**
     * Wrap standard Selenium WebElement collection into SelenideElement collection
     * to use additional methods like shouldHave() etc.
     *
     * @param elements standard Selenium WebElement collection
     * @return given WebElement collection wrapped into SelenideElement collection
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun elements(elements: Collection<WebElement>): ElementsCollection {
        return WebDriverRunner.selenideDriver.`$$`(elements)
    }

    /**
     * Locates all elements matching given CSS selector.
     * ATTENTION! This method doesn't start any search yet!
     * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
     * and at the same time is implementation of WebElement interface,
     * meaning that you can call methods .sendKeys(), click() etc. on it.
     *
     * @param cssSelector any CSS selector like "input[name='first_name']" or "#messages .new_message"
     * @return empty list if element was no found
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun elements(cssSelector: String): ElementsCollection {
        return WebDriverRunner.selenideDriver.`$$`(cssSelector)
    }

    /**
     * Locates all elements matching given CSS selector.
     * ATTENTION! This method doesn't start any search yet!
     * Methods returns an ElementsCollection which is a list of WebElement objects that can be iterated,
     * and at the same time is implementation of WebElement interface,
     * meaning that you can call methods .sendKeys(), click() etc. on it.
     *
     * @param seleniumSelector any Selenium selector like By.id(), By.name() etc.
     * @return empty list if element was no found
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun elements(seleniumSelector: By): ElementsCollection {
        return WebDriverRunner.selenideDriver.`$$`(seleniumSelector)
    }

    /**
     * @param criteria instance of By: By.id(), By.className() etc.
     * @return SelenideElement
     */
    @CheckReturnValue
    @Deprecated(
        """please use element(criteria) which is the same
    (method will not be removed until 4.x or later)
    Locates the first element matching given criteria
    ATTENTION! This method doesn't start any search yet!
    """
    )
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun getElement(criteria: By): SelenideElement {
        return WebDriverRunner.selenideDriver.find(criteria)
    }

    /**
     * @param criteria instance of By: By.id(), By.className() etc.
     * @param index 0..N
     * @return SelenideElement
     */
    @CheckReturnValue
    @Deprecated(
        """please use element(criteria, index) which is the same
    (method will not be removed until 4.x or later)
    Locates the Nth element matching given criteria
    ATTENTION! This method doesn't start any search yet!
    """
    )
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun getElement(criteria: By, index: Int): SelenideElement {
        return WebDriverRunner.selenideDriver.find(criteria, index)
    }

    /**
     * @param criteria instance of By: By.id(), By.className() etc.
     * @return empty list if element was no found
     */
    @CheckReturnValue
    @Deprecated(
        """please use elements(criteria) which is the same
    (method will not be removed until 4.x or later)
    Locates all elements matching given CSS selector
    ATTENTION! This method doesn't start any search yet!
    """
    )
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun getElements(criteria: By): ElementsCollection {
        return WebDriverRunner.selenideDriver.findAll(criteria)
    }

    /**
     * @see JavascriptExecutor.executeScript
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun <T> executeJavaScript(jsCode: String, vararg arguments: Any): T? {
        return WebDriverRunner.selenideDriver.executeJavaScript(jsCode, *arguments)
    }

    /**
     * @see JavascriptExecutor.executeAsyncScript
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun <T> executeAsyncJavaScript(jsCode: String, vararg arguments: Any): T? {
        return WebDriverRunner.selenideDriver.executeAsyncJavaScript(jsCode, *arguments)
    }

    /**
     * Returns selected element in radio group
     * @return null if nothing selected
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun getSelectedRadio(radioField: By): SelenideElement? {
        return WebDriverRunner.selenideDriver.getSelectedRadio(radioField)
    }

    /**
     * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'alert' or 'confirm').
     * @return actual dialog text
     */
    @JvmStatic
    @kotlin.time.ExperimentalTime
    @okio.ExperimentalFileSystem
    suspend fun confirm(): String {
        return WebDriverRunner.selenideDriver.modal().confirm()
    }

    /**
     * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'alert' or 'confirm').
     *
     * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
     * @throws DialogTextMismatch if confirmation message differs from expected message
     * @return actual dialog text
     */
    @JvmStatic
    @kotlin.time.ExperimentalTime
    @okio.ExperimentalFileSystem
    suspend fun confirm(expectedDialogText: String): String {
        return WebDriverRunner.selenideDriver.modal().confirm(expectedDialogText)
    }

    /**
     * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'prompt').
     * @return actual dialog text
     */
    @JvmStatic
    @kotlin.time.ExperimentalTime
    @okio.ExperimentalFileSystem
    suspend fun prompt(): String {
        return WebDriverRunner.selenideDriver.modal().prompt()
    }

    /**
     * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'prompt').
     * @param inputText if not null, sets value in prompt dialog input
     * @return actual dialog text
     */
    @JvmStatic
    @kotlin.time.ExperimentalTime
    @okio.ExperimentalFileSystem
    suspend fun prompt(inputText: String): String {
        return WebDriverRunner.selenideDriver.modal().prompt(inputText)
    }

    /**
     * Accept (Click "Yes" or "Ok") in the confirmation dialog (javascript 'prompt').
     *
     * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
     * @param inputText if not null, sets value in prompt dialog input
     * @throws DialogTextMismatch if confirmation message differs from expected message
     * @return actual dialog text
     */
    @JvmStatic
    @kotlin.time.ExperimentalTime
    @okio.ExperimentalFileSystem
    suspend fun prompt(expectedDialogText: String, inputText: String): String? {
        return WebDriverRunner.selenideDriver.modal().prompt(expectedDialogText, inputText)
    }

    /**
     * Dismiss (click "No" or "Cancel") in the confirmation dialog (javascript 'alert' or 'confirm').
     * @return actual dialog text
     */
    @JvmStatic
    @kotlin.time.ExperimentalTime
    @okio.ExperimentalFileSystem
    suspend fun dismiss(): String? {
        return WebDriverRunner.selenideDriver.modal().dismiss()
    }

    /**
     * Dismiss (click "No" or "Cancel") in the confirmation dialog (javascript 'alert' or 'confirm').
     *
     * @param expectedDialogText if not null, check that confirmation dialog displays this message (case-sensitive)
     * @throws DialogTextMismatch if confirmation message differs from expected message
     * @return actual dialog text
     */
    @JvmStatic
    @kotlin.time.ExperimentalTime
    @okio.ExperimentalFileSystem
    suspend fun dismiss(expectedDialogText: String): String? {
        return WebDriverRunner.selenideDriver.modal().dismiss(expectedDialogText)
    }

    /**
     * Switch to window/tab/frame/parentFrame/innerFrame/alert.
     * Allows switching to window by title, index, name etc.
     *
     * Similar to org.openqa.selenium.WebDriver#switchTo(), but all methods wait until frame/window/alert
     * appears if it's not visible yet (like other Selenide methods).
     *
     * @return SelenideTargetLocator
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun switchTo(): SelenideTargetLocator {
        return WebDriverRunner.selenideDriver.driver().switchTo()
    }

    /**
     *
     * @return WebElement, not SelenideElement! which has focus on it
     */
    @okio.ExperimentalFileSystem
    @CheckReturnValue
    suspend fun getFocusedElement(): WebElement? = WebDriverRunner.selenideDriver.getFocusedElement()

    /**
     * Create a Page Object instance
     */
    /* TODO: @CheckReturnValue
    @JvmStatic
    fun <PageObjectClass: Any> page(pageObjectClass: Class<PageObjectClass>): PageObjectClass {
        return WebDriverRunner.selenideDriver.page(pageObjectClass)
    }

    *//**
     * Initialize a given Page Object instance
     *//*
    @CheckReturnValue
    @JvmStatic
    fun <PageObjectClass: Any, T : PageObjectClass> page(pageObject: T): PageObjectClass {
        return WebDriverRunner.selenideDriver.page(pageObject)
    }*/

    /**
     * Create a org.openqa.selenium.support.ui.FluentWait instance with Selenide timeout/polling.
     *
     * Sample usage:
     * `Wait().until(invisibilityOfElementLocated(By.id("magic-id")));
    ` *
     *
     * @return instance of org.openqa.selenium.support.ui.FluentWait
     */
    @CheckReturnValue
    @JvmStatic
    @kotlin.time.ExperimentalTime
    @okio.ExperimentalFileSystem
    fun Wait(): SelenideWait {
        return WebDriverRunner.selenideDriver.Wait()
    }

    /**
     * With this method you can use Selenium Actions like described in the
     * [AdvancedUserInteractions](http://code.google.com/p/selenium/wiki/AdvancedUserInteractions) page.
     *
     * <pre>
     * actions()
     * .sendKeys($(By.name("rememberMe")), "John")
     * .click($(#rememberMe"))
     * .click($(byText("Login")))
     * .build()
     * .perform();
    </pre> *
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun actions(): Actions {
        return WebDriverRunner.selenideDriver.driver().actions()
    }

    /**
     * Zoom current page (in or out).
     * @param factor e.g. 1.1 or 2.0 or 0.5
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun zoom(factor: Double) {
        WebDriverRunner.selenideDriver.zoom(factor)
    }

    /**
     * Same as com.codeborne.selenide.Selenide#getWebDriverLogs(java.lang.String, java.util.logging.Level)
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun getWebDriverLogs(logType: String): List<String> {
        return WebDriverRunner.selenideDriver.webDriverLogs.logs(logType)
    }

    /**
     * Getting and filtering of the WebDriver logs for specified LogType by specified logging level
     * <br></br>
     * For example to get WebDriver Browser's console output (including JS info, warnings, errors, etc. messages)
     * you can use:
     * <br></br>
     * <pre>
     * `for(String logEntry : getWebDriverLogs(LogType.BROWSER, Level.ALL)) {
     * Reporter.log(logEntry + "<br>");
     * }
    ` *
    </pre> *
     * <br></br>
     * Be aware that currently "manage().logs()" is in the Beta stage, but it is beta-then-nothing :)
     * <br></br>
     * List of the unsupported browsers and issues:
     * <br></br>
     * http://bit.ly/RZcmrM
     * <br></br>
     * http://bit.ly/1nZTaqu
     * <br></br>
     *
     * @param logType WebDriver supported log types
     * @param logLevel logging level that will be used to control logging output
     * @return list of log entries
     * @see LogType
     *
     * @see Level
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun getWebDriverLogs(logType: String, logLevel: Level): List<String> {
        return WebDriverRunner.selenideDriver.webDriverLogs.logs(logType, logLevel)
    }

    /**
     * Clear browser cookies.
     *
     * It can be useful e.g. if you are trying to avoid restarting browser between tests
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun clearBrowserCookies() {
        WebDriverRunner.selenideDriver.clearCookies()
    }

    /**
     * Clear browser local storage.
     *
     * In case if you need to be sure that browser's localStorage is empty
     */
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun clearBrowserLocalStorage() {
        WebDriverRunner.selenideDriver.clearBrowserLocalStorage()
    }

    /**
     * Get current user agent from browser session
     *
     * @return browser user agent
     */
    @CheckReturnValue
    @okio.ExperimentalFileSystem
    suspend fun getUserAgent(): String = WebDriverRunner.selenideDriver.driver().getUserAgent()

    /**
     * Return true if bottom of the page is reached
     *
     * Useful if you need to scroll down by x pixels unknown number of times.
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun atBottom(): Boolean {
        return WebDriverRunner.selenideDriver.atBottom()
    }

    /**
     * NB! URL must be properly encoded.
     * E.g. instead of "/files/ж.txt", it should be "/files/%D0%B6.txt"
     *
     * @see .download
     */
/* TODO:
    @CheckReturnValue
    @Throws(IOException::class, URISyntaxException::class)
    @JvmStatic
    @okio.ExperimentalFileSystem
    suspend fun download(url: String): Path {
        return WebDriverRunner.selenideDriver.download(url)
    }
*/

    /**
     * @see .download
     */
/*
TODO:    @CheckReturnValue
    @Throws(IOException::class)
    @JvmStatic
    suspend fun download(url: URI): Path {
        return WebDriverRunner.selenideDriver.download(url)
    }
*/

    /**
     * @see .download
     */
/*
TODO:    @CheckReturnValue
    @Throws(IOException::class)
    @JvmStatic
    suspend fun download(url: URI, timeoutMs: Long): Path {
        return WebDriverRunner.selenideDriver.download(url, timeoutMs)
    }
*/

    /**
     * Download file using a direct link.
     * This method download file like it would be done in currently opened browser:
     * it adds all cookies and "User-Agent" header to the downloading request.
     *
     * Download fails if specified timeout is exceeded
     *
     * @param url either relative or absolute url
     * NB! URL must be properly encoded.
     * E.g. instead of "/files/ж.txt", it should be "/files/%D0%B6.txt"
     * @param timeoutMs specific timeout in ms
     * @return downloaded Path in folder `Configuration.reportsFolder`
     * @throws IOException if failed to download file
     * @throws URISyntaxException if given url has invalid syntax
     */
/*
TODO:    @CheckReturnValue
    @Throws(IOException::class, URISyntaxException::class)
    @JvmStatic
    suspend fun download(url: String, timeoutMs: Long): Path {
        return WebDriverRunner.selenideDriver.download(URI(url), timeoutMs)
    }
*/

    /**
     * Access browser's local storage.
     * Allows setting, getting, removing items as well as getting the size and clear the storage.
     *
     * @return LocalStorage
     * @since 5.15.0
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun localStorage(): LocalStorage {
        return WebDriverRunner.selenideDriver.localStorage
    }

    /**
     * Access browser's session storage.
     * Allows setting, getting, removing items as well as getting the size, check for contains item and clear the storage.
     *
     * @return sessionStorage
     * @since 5.18.1
     */
    @CheckReturnValue
    @JvmStatic
    @okio.ExperimentalFileSystem
    fun sessionStorage(): SessionStorage {
        return WebDriverRunner.selenideDriver.sessionStorage
    }

    /**
     * Provide access to system clipboard, allows get and set String content.
     * Default implementation acts via [java.awt.Toolkit] and supports only local runs.
     *
     * Remote runs support can be implemented via plugins.
     * Plugin for Selenoid will be released soon.
     *
     * Pay attention that Clipboard is shared resource for instance where tests runs
     * and keep in mind while developing test suite with multiple tests for clipboard.
     *
     * @return Clipboard
     */
/*
TODO:    @CheckReturnValue
    @JvmStatic
    fun clipboard(): Clipboard {
        return WebDriverRunner.selenideDriver.clipboard
    }
*/
}

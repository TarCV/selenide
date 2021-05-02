package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideTargetLocator
import com.codeborne.selenide.impl.FileHelper.ensureParentFolderExists
import com.codeborne.selenide.impl.Plugins.inject
import com.codeborne.selenide.impl.Screenshot.Companion.none
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory
import support.URLEncoder
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Function

open class ScreenShotLaboratory constructor(
    private val photographer: Photographer = inject(
        Photographer::class
    ),
    private val extractor: PageSourceExtractor = inject(PageSourceExtractor::class),
    private val clock: Clock = Clock()
) {
    private val allScreenshots: MutableList<File> = java.util.ArrayList()
    private var screenshotCounter = AtomicLong()
    protected var currentContext: ThreadLocal<String> = ThreadLocal.withInitial { "" }
    private var currentContextScreenshots = ThreadLocal<MutableList<File>>()
    private val _threadScreenshots: ThreadLocal<MutableList<File>> = ThreadLocal.withInitial { ArrayList() }

    protected val threadScreenshots: List<File>
        get() {
          val screenshots: List<File>? = _threadScreenshots.get()
          return if (screenshots == null) emptyList() else (screenshots)
        }
    fun takeScreenShot(driver: Driver, className: String, methodName: String): Screenshot {
        return takeScreenshot(driver, getScreenshotFileName(className, methodName))
    }
    protected fun getScreenshotFileName(className: String, methodName: String): String {
        return className.replace('.', File.separatorChar) + File.separatorChar +
                methodName + '.' + clock.timestamp()
    }
    @Deprecated("use {@link #takeScreenshot(Driver)} which returns {@link Screenshot} instead of String")
    open fun takeScreenShot(driver: Driver): String? {
        return takeScreenShot(driver, generateScreenshotFileName())
    }

    /**
     * Takes screenshot of current browser window.
     * Stores 2 files: html of page (if "savePageSource" option is enabled), and (if possible) image in PNG format.
     *
     * @param fileName name of file (without extension) to store screenshot to.
     * @return the name of last saved screenshot or null if failed to create screenshot
     */
    @Deprecated("use {@link #takeScreenshot(Driver, String)} which returns {@link Screenshot} instead of String")
    fun takeScreenShot(driver: Driver, fileName: String): String? {
        return takeScreenshot(driver, fileName).image
    }

    /**
     * Takes screenshot of current browser window.
     * Stores 2 files: html of page (if "savePageSource" option is enabled), and (if possible) image in PNG format.
     *
     * @param fileName name of file (without extension) to store screenshot to.
     * @return instance of [Screenshot] containing both files
     */
    fun takeScreenshot(driver: Driver, fileName: String): Screenshot {
        val screenshot = ifWebDriverStarted(
            driver,
            Function { webDriver: WebDriver? ->
                ifReportsFolderNotNull(driver.config()) { config: Config ->
                    takeScreenShot(
                        config,
                        driver,
                        fileName
                    )
                }
            })
        return screenshot ?: none()
    }
    fun <T: Any> takeScreenShot(driver: Driver, outputType: OutputType<T>): T? {
        return ifWebDriverStarted(driver) {
            photographer.takeScreenshot(driver, outputType)
                .map { screenshot: T -> addToHistoryIfFile(screenshot, outputType) }
                .orElse(null)
        }
    }

    private fun <T> addToHistoryIfFile(screenshot: T, outputType: OutputType<T>): T {
        if (outputType === OutputType.FILE) {
            addToHistory(screenshot as File)
        }
        return screenshot
    }
    private fun takeScreenShot(config: Config, driver: Driver, fileName: String): Screenshot {
        val source = if (config.savePageSource()) savePageSourceToFile(config, fileName, driver) else null
        val image = if (config.screenshots()) savePageImageToFile(config, fileName, driver) else null
        image?.let { addToHistory(it) }
        return Screenshot(toUrl(config, image), toUrl(config, source))
    }
    fun takeScreenshot(driver: Driver, element: WebElement): File? {
        try {
            val destination = takeScreenshotAsImage(driver, element)
            if (destination != null) {
                return writeToFile(driver, destination)
            }
        } catch (e: IOException) {
            log.error("Failed to take screenshot of ${}", element, e)
        }
        return null
    }
    fun takeScreenshotAsImage(driver: Driver, element: WebElement): BufferedImage? {
        return ifWebDriverStarted(driver) { webdriver: WebDriver? ->
            ifReportsFolderNotNull(driver.config()) { config: Config? ->
                takeElementScreenshotAsImage(
                    driver,
                    element
                ).orElse(null)
            }
        }
    }
    private fun takeElementScreenshotAsImage(driver: Driver, element: WebElement): BufferedImage? {
        if (driver.webDriver !is TakesScreenshot) {
            log.warn("Cannot take screenshot because browser does not support screenshots")
            return null
        }
        return photographer.takeScreenshot(driver, OutputType.BYTES)
            ?.let { screen: ByteArray -> cropToElement(screen, element) }
    }
    private fun cropToElement(screen: ByteArray, element: WebElement): BufferedImage? {
        val elementLocation = element.location
        return try {
            val img = ImageIO.read(ByteArrayInputStream(screen))
            val elementWidth = getRescaledElementWidth(element, img)
            val elementHeight = getRescaledElementHeight(element, img)
            (img.getSubimage(elementLocation.getX(), elementLocation.getY(), elementWidth, elementHeight))
        } catch (e: IOException) {
            log.error("Failed to take screenshot of ${}", element, e)
            null
        } catch (e: RasterFormatException) {
            log.warn("Cannot take screenshot because element is not displayed on current screen position")
            null
        }
    }
    protected fun generateScreenshotFileName(): String {
        return currentContext.get() + clock.timestamp() + "." + screenshotCounter.getAndIncrement()
    }
    fun takeScreenshot(driver: Driver, iframe: WebElement, element: WebElement): File? {
        try {
            val destination = takeScreenshotAsImage(driver, iframe, element)
            if (destination != null) {
                return writeToFile(driver, destination)
            }
        } catch (e: IOException) {
            log.error("Failed to take screenshot of ${} inside frame ${}", element, iframe, e)
        }
        return null
    }
    private fun writeToFile(driver: Driver, destination: BufferedImage): File {
        val screenshotOfElement =
            File(driver.config().reportsFolder(), generateScreenshotFileName() + ".png").absoluteFile
        ensureParentFolderExists(screenshotOfElement)
        ImageIO.write(destination, "png", screenshotOfElement)
        return screenshotOfElement
    }
    fun takeScreenshotAsImage(driver: Driver, iframe: WebElement, element: WebElement): BufferedImage? {
        val webdriver = checkIfFullyValidDriver(driver) ?: return null
        val screenshot = photographer.takeScreenshot(driver, OutputType.BYTES)
        return screenshot.flatMap { screen: ByteArray -> takeScreenshotAsImage(driver, iframe, element, screen) }
            .orElse(null)
    }
    private fun takeScreenshotAsImage(
        driver: Driver, iframe: WebElement,
        element: WebElement, screen: ByteArray
    ): BufferedImage? {
        val iframeLocation = iframe.location
        var img: BufferedImage
        img = try {
            ImageIO.read(ByteArrayInputStream(screen))
        } catch (e: IOException) {
            log.error("Failed to take screenshot of ${} inside frame ${}", element, iframe, e)
            return null
        } catch (ex: RasterFormatException) {
            log.warn("Cannot take screenshot because iframe is not displayed")
            return null
        }
        val iframeHeight = getRescaledElementHeight(iframe, img)
        val switchTo = SelenideTargetLocator(driver)
        switchTo.frame(iframe)
        val iframeWidth = getRescaledIframeWidth(iframe, img, driver.webDriver)
        val elementLocation = element.location
        val elementWidth = getRescaledElementWidth(element, iframeWidth)
        val elementHeight = getRescaledElementHeight(element, iframeHeight)
        switchTo.defaultContent()
        img = try {
            img.getSubimage(
                iframeLocation.getX() + elementLocation.getX(), iframeLocation.getY() + elementLocation.getY(),
                elementWidth, elementHeight
            )
        } catch (ex: RasterFormatException) {
            log.warn("Cannot take screenshot because element is not displayed in iframe")
            return null
        }
        return (img)
    }
    private fun checkIfFullyValidDriver(driver: Driver): WebDriver? {
        return ifWebDriverStarted(driver) { webdriver: WebDriver -> this.checkIfFullyValidDriver(webdriver) }
    }
    private fun checkIfFullyValidDriver(webdriver: WebDriver): WebDriver? {
        if (webdriver !is TakesScreenshot) {
            log.warn("Cannot take screenshot because browser does not support screenshots")
            return null
        } else if (webdriver !is JavascriptExecutor) {
            log.warn("Cannot take screenshot as driver is not supporting javascript execution")
            return null
        }
        return webdriver
    }
    fun takeScreenShotAsFile(driver: Driver): File? {
        return ifWebDriverStarted<File>(driver) { webDriver: WebDriver? ->
            //File pageSource = savePageSourceToFile(fileName, webDriver); - temporary not available
            try {
                return@ifWebDriverStarted photographer.takeScreenshot(driver, OutputType.FILE)
                    .map { screenshot: File -> addToHistory(screenshot) }
                    .orElse(null)
            } catch (e: Exception) {
                log.error("Failed to take screenshot in memory", e)
                return@ifWebDriverStarted null
            }
        }
    }

    protected fun addToHistory(screenshot: File): File {

        currentContextScreenshots.get()?.add(screenshot)
        synchronized(allScreenshots) { allScreenshots.add(screenshot) }
        _threadScreenshots.get().add(screenshot)
        return screenshot
    }
    protected fun savePageImageToFile(config: Config, fileName: String, driver: Driver): File? {
        return try {
            val scrFile = photographer.takeScreenshot(driver, OutputType.BYTES)
            if (!scrFile.isPresent) {
                log.info("Webdriver doesn't support screenshots")
                return null
            }
            val imageFile = File(config.reportsFolder(), "$fileName.png").absoluteFile
            try {
                writeToFile(scrFile.get(), imageFile)
            } catch (e: IOException) {
                log.error("Failed to save screenshot to ${}", imageFile, e)
            }
            imageFile
        } catch (e: WebDriverException) {
            log.error("Failed to take screenshot to ${}", fileName, e)
            null
        }
    }
    protected fun savePageSourceToFile(config: Config, fileName: String, driver: Driver): File {
        return extractor.extract(config, driver.webDriver, fileName)
    }

    fun startContext(className: String, methodName: String) {
        val context = className.replace('.', File.separatorChar) + File.separatorChar + methodName + File.separatorChar
        startContext(context)
    }

    fun startContext(context: String) {
        currentContext.set(context)
        currentContextScreenshots.set(java.util.ArrayList())
    }

    fun finishContext(): List<File> {
        val result: List<File> = currentContextScreenshots.get()
        currentContext.set("")
        currentContextScreenshots.remove()
        return result
    }
    val screenshots: List<File>
        get() {
            synchronized(allScreenshots) { return (allScreenshots) }
        }
    val contextScreenshots: List<File>
        get() {
            val screenshots: List<File>? = currentContextScreenshots.get()
            return if (screenshots == null) emptyList() else (screenshots)
        }
    val lastScreenshot: File?
        get() {
            synchronized(allScreenshots) { return if (allScreenshots.isEmpty()) null else allScreenshots[allScreenshots.size - 1] }
        }
    val lastThreadScreenshot: File?
        get() {
            val screenshots: List<File> = _threadScreenshots.get()
            return getLastScreenshot(screenshots)
        }
    val lastContextScreenshot: File?
        get() {
            val screenshots: List<File>? = currentContextScreenshots.get()
            return getLastScreenshot(screenshots)
        }
    private fun getLastScreenshot(screenshots: List<File>?): File? {
        return if (screenshots == null || screenshots.isEmpty()) null else (
            screenshots[screenshots.size - 1]
        )
    }
    @Deprecated("Use method {@link #takeScreenshot(Driver)} which returns Screenshot instead of String")
    fun formatScreenShotPath(driver: Driver): String {
        return StringUtils.defaultString(takeScreenshot(driver).image, "")
    }
    fun takeScreenshot(driver: Driver): Screenshot {
        val screenshot = ifWebDriverStarted(
            driver
        ) {
            ifReportsFolderNotNull(driver.config()) { config: Config ->
                takeScreenShot(
                    config,
                    driver,
                    generateScreenshotFileName()
                )
            }
        }
      return screenshot ?: none()
    }
    private fun toUrl(config: Config, file: File?): String? {
        if (file == null) {
            return null
        } else {
            config.reportsUrl()?.let {
                return formatScreenShotURL(it, file.absolutePath)
            }
        }
        return try {
            file.canonicalFile.toURI().toURL().toExternalForm()
        } catch (e: IOException) {
            "file://" + file.absolutePath
        }
    }
    private fun formatScreenShotURL(reportsURL: String, screenshot: String): String {
        val current = Paths.get(System.getProperty("user.dir"))
        val target = Paths.get(screenshot).normalize()
        val screenShotPath: String = if (isInsideFolder(current, target)) {
            current.relativize(target).toString().replace('\\', '/')
        } else {
            target.toFile().name
        }
        return normalizeURL(reportsURL, screenShotPath)
    }
    private fun normalizeURL(reportsURL: String, path: String): String {
        return appendSlash(reportsURL) + encodePath(path)
    }
    private fun appendSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }
    fun encodePath(path: String): String {
        return REGEX_PLUS
            .matcher(
                Arrays.stream(path.split("/").toTypedArray())
                    .map { str: String -> encode(str) }
                    .joinToString("/")
            ).replaceAll("%20")
    }
    private fun encode(str: String): String {
        return try {
            URLEncoder.encode(str, StandardCharsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            log.debug("Cannot encode path segment: ${}", str, e)
            str
        }
    }
    private fun <T> ifWebDriverStarted(driver: Driver, lambda: Function<WebDriver, T?>): T? {
        if (!driver.hasWebDriverStarted()) {
            log.warn("Cannot take screenshot because browser is not started")
            return null
        }
        return lambda.apply(driver.webDriver)
    }
    private fun <T> ifReportsFolderNotNull(config: Config, lambda: Function<Config, T>): T? {
        if (config.reportsFolder() == null) {
            log.error("Cannot take screenshot because reportsFolder is null")
            return null
        }
        return lambda.apply(config)
    }
    private fun getRescaledElementWidth(element: WebElement, iframeWidth: Int): Int {
        val elementWidth = getElementWidth(element)
        return if (elementWidth > iframeWidth) {
            iframeWidth - element.location.getX()
        } else {
            elementWidth
        }
    }
    private fun getRescaledElementHeight(element: WebElement, iframeHeight: Int): Int {
        val elementHeight = getElementHeight(element)
        return if (elementHeight > iframeHeight) {
            iframeHeight - element.location.getY()
        } else {
            elementHeight
        }
    }
    private fun getRescaledElementWidth(element: WebElement, image: BufferedImage): Int {
        return if (getElementWidth(element) > image.width) {
            image.width - element.location.getX()
        } else {
            getElementWidth(element)
        }
    }
    private fun getRescaledElementHeight(element: WebElement, image: BufferedImage): Int {
        return if (getElementHeight(element) > image.height) {
            image.height - element.location.getY()
        } else {
            getElementHeight(element)
        }
    }
    private fun getRescaledIframeWidth(iframe: WebElement, image: BufferedImage, driver: WebDriver): Int {
        return if (getIframeWidth(driver) > image.width) {
            image.width - iframe.location.getX()
        } else {
            getIframeWidth(driver)
        }
    }
    private fun getIframeWidth(driver: WebDriver): Int {
        return ((driver as JavascriptExecutor).executeScript("return document.body.clientWidth") as Long).toInt()
    }
    private fun getElementWidth(element: WebElement): Int {
        return element.size.getWidth()
    }
    private fun getElementHeight(element: WebElement): Int {
        return element.size.getHeight()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ScreenShotLaboratory::class)
        val instance = ScreenShotLaboratory()
        private val REGEX_PLUS = kotlin.text.Regex("\\+")
        private fun isInsideFolder(root: Path, other: Path): Boolean {
            return other.startsWith(root.toAbsolutePath())
        }
    }
}

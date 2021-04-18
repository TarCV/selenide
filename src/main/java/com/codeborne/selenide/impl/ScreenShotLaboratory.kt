package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideTargetLocator
import com.codeborne.selenide.impl.FileHelper.ensureParentFolderExists
import com.codeborne.selenide.impl.FileHelper.writeToFile
import com.codeborne.selenide.impl.Plugins.inject
import com.codeborne.selenide.impl.Screenshot.Companion.none
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.awt.image.RasterFormatException
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Arrays
import java.util.Collections
import java.util.Optional
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Function
import java.util.regex.Pattern
import java.util.stream.Collectors
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault
import javax.imageio.ImageIO

@ParametersAreNonnullByDefault
open class ScreenShotLaboratory @JvmOverloads constructor(
    private val photographer: Photographer = inject(
        Photographer::class.java
    ),
    private val extractor: PageSourceExtractor = inject(PageSourceExtractor::class.java),
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
          return if (screenshots == null) emptyList() else Collections.unmodifiableList(screenshots)
        }


    @CheckReturnValue
    fun takeScreenShot(driver: Driver, className: String, methodName: String): Screenshot {
        return takeScreenshot(driver, getScreenshotFileName(className, methodName))
    }

    @CheckReturnValue
    protected fun getScreenshotFileName(className: String, methodName: String): String {
        return className.replace('.', File.separatorChar) + File.separatorChar +
                methodName + '.' + clock.timestamp()
    }

    @CheckReturnValue
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
    @CheckReturnValue
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
    @CheckReturnValue
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

    @CheckReturnValue
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

    @CheckReturnValue
    private fun takeScreenShot(config: Config, driver: Driver, fileName: String): Screenshot {
        val source = if (config.savePageSource()) savePageSourceToFile(config, fileName, driver) else null
        val image = if (config.screenshots()) savePageImageToFile(config, fileName, driver) else null
        image?.let { addToHistory(it) }
        return Screenshot(toUrl(config, image), toUrl(config, source))
    }

    @CheckReturnValue
    fun takeScreenshot(driver: Driver, element: WebElement): File? {
        try {
            val destination = takeScreenshotAsImage(driver, element)
            if (destination != null) {
                return writeToFile(driver, destination)
            }
        } catch (e: IOException) {
            log.error("Failed to take screenshot of {}", element, e)
        }
        return null
    }

    @CheckReturnValue
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

    @CheckReturnValue
    private fun takeElementScreenshotAsImage(driver: Driver, element: WebElement): Optional<BufferedImage> {
        if (driver.webDriver !is TakesScreenshot) {
            log.warn("Cannot take screenshot because browser does not support screenshots")
            return Optional.empty()
        }
        return photographer.takeScreenshot(driver, OutputType.BYTES)
            .flatMap { screen: ByteArray -> cropToElement(screen, element) }
    }

    @CheckReturnValue
    private fun cropToElement(screen: ByteArray, element: WebElement): Optional<BufferedImage> {
        val elementLocation = element.location
        return try {
            val img = ImageIO.read(ByteArrayInputStream(screen))
            val elementWidth = getRescaledElementWidth(element, img)
            val elementHeight = getRescaledElementHeight(element, img)
            Optional.of(img.getSubimage(elementLocation.getX(), elementLocation.getY(), elementWidth, elementHeight))
        } catch (e: IOException) {
            log.error("Failed to take screenshot of {}", element, e)
            Optional.empty()
        } catch (e: RasterFormatException) {
            log.warn("Cannot take screenshot because element is not displayed on current screen position")
            Optional.empty()
        }
    }

    @CheckReturnValue
    protected fun generateScreenshotFileName(): String {
        return currentContext.get() + clock.timestamp() + "." + screenshotCounter.getAndIncrement()
    }

    @CheckReturnValue
    fun takeScreenshot(driver: Driver, iframe: WebElement, element: WebElement): File? {
        try {
            val destination = takeScreenshotAsImage(driver, iframe, element)
            if (destination != null) {
                return writeToFile(driver, destination)
            }
        } catch (e: IOException) {
            log.error("Failed to take screenshot of {} inside frame {}", element, iframe, e)
        }
        return null
    }

    @CheckReturnValue
    @Throws(IOException::class)
    private fun writeToFile(driver: Driver, destination: BufferedImage): File {
        val screenshotOfElement =
            File(driver.config().reportsFolder(), generateScreenshotFileName() + ".png").absoluteFile
        ensureParentFolderExists(screenshotOfElement)
        ImageIO.write(destination, "png", screenshotOfElement)
        return screenshotOfElement
    }

    @CheckReturnValue
    fun takeScreenshotAsImage(driver: Driver, iframe: WebElement, element: WebElement): BufferedImage? {
        val webdriver = checkIfFullyValidDriver(driver) ?: return null
        val screenshot = photographer.takeScreenshot(driver, OutputType.BYTES)
        return screenshot.flatMap { screen: ByteArray -> takeScreenshotAsImage(driver, iframe, element, screen) }
            .orElse(null)
    }

    @CheckReturnValue
    private fun takeScreenshotAsImage(
        driver: Driver, iframe: WebElement,
        element: WebElement, screen: ByteArray
    ): Optional<BufferedImage> {
        val iframeLocation = iframe.location
        var img: BufferedImage
        img = try {
            ImageIO.read(ByteArrayInputStream(screen))
        } catch (e: IOException) {
            log.error("Failed to take screenshot of {} inside frame {}", element, iframe, e)
            return Optional.empty()
        } catch (ex: RasterFormatException) {
            log.warn("Cannot take screenshot because iframe is not displayed")
            return Optional.empty()
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
            return Optional.empty()
        }
        return Optional.of(img)
    }

    @CheckReturnValue
    private fun checkIfFullyValidDriver(driver: Driver): WebDriver? {
        return ifWebDriverStarted(driver) { webdriver: WebDriver -> this.checkIfFullyValidDriver(webdriver) }
    }

    @CheckReturnValue
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

    @CheckReturnValue
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
        if (currentContextScreenshots.get() != null) {
            currentContextScreenshots.get()!!.add(screenshot)
        }
        synchronized(allScreenshots) { allScreenshots.add(screenshot) }
        _threadScreenshots.get().add(screenshot)
        return screenshot
    }

    @CheckReturnValue
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
                log.error("Failed to save screenshot to {}", imageFile, e)
            }
            imageFile
        } catch (e: WebDriverException) {
            log.error("Failed to take screenshot to {}", fileName, e)
            null
        }
    }

    @CheckReturnValue
    protected fun savePageSourceToFile(config: Config?, fileName: String?, driver: Driver): File {
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

    @get:CheckReturnValue
    val screenshots: List<File>
        get() {
            synchronized(allScreenshots) { return Collections.unmodifiableList(allScreenshots) }
        }

    @get:CheckReturnValue
    val contextScreenshots: List<File>
        get() {
            val screenshots: List<File>? = currentContextScreenshots.get()
            return if (screenshots == null) emptyList() else Collections.unmodifiableList(screenshots)
        }

    @get:CheckReturnValue
    val lastScreenshot: File?
        get() {
            synchronized(allScreenshots) { return if (allScreenshots.isEmpty()) null else allScreenshots[allScreenshots.size - 1] }
        }

    @get:CheckReturnValue
    val lastThreadScreenshot: Optional<File>
        get() {
            val screenshots: List<File> = _threadScreenshots.get()
            return getLastScreenshot(screenshots)
        }

    @get:CheckReturnValue
    val lastContextScreenshot: Optional<File>
        get() {
            val screenshots: List<File>? = currentContextScreenshots.get()
            return getLastScreenshot(screenshots)
        }

    @CheckReturnValue
    private fun getLastScreenshot(screenshots: List<File>?): Optional<File> {
        return if (screenshots == null || screenshots.isEmpty()) Optional.empty() else Optional.of(
            screenshots[screenshots.size - 1]
        )
    }

    @CheckReturnValue
    @Deprecated("Use method {@link #takeScreenshot(Driver)} which returns Screenshot instead of String")
    fun formatScreenShotPath(driver: Driver): String {
        return StringUtils.defaultString(takeScreenshot(driver).image, "")
    }

    @CheckReturnValue
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

    @CheckReturnValue
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

    @CheckReturnValue
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

    @CheckReturnValue
    private fun normalizeURL(reportsURL: String, path: String): String {
        return appendSlash(reportsURL) + encodePath(path)
    }

    @CheckReturnValue
    private fun appendSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    @CheckReturnValue
    fun encodePath(path: String): String {
        return REGEX_PLUS.matcher(
            Arrays.stream(path.split("/").toTypedArray())
                .map { str: String -> encode(str) }
                .collect(Collectors.joining("/"))).replaceAll("%20")
    }

    @CheckReturnValue
    private fun encode(str: String): String {
        return try {
            URLEncoder.encode(str, StandardCharsets.UTF_8.name())
        } catch (e: UnsupportedEncodingException) {
            log.debug("Cannot encode path segment: {}", str, e)
            str
        }
    }

    @CheckReturnValue
    private fun <T> ifWebDriverStarted(driver: Driver, lambda: Function<WebDriver, T?>): T? {
        if (!driver.hasWebDriverStarted()) {
            log.warn("Cannot take screenshot because browser is not started")
            return null
        }
        return lambda.apply(driver.webDriver)
    }

    @CheckReturnValue
    private fun <T> ifReportsFolderNotNull(config: Config, lambda: Function<Config, T>): T? {
        if (config.reportsFolder() == null) {
            log.error("Cannot take screenshot because reportsFolder is null")
            return null
        }
        return lambda.apply(config)
    }

    @CheckReturnValue
    private fun getRescaledElementWidth(element: WebElement, iframeWidth: Int): Int {
        val elementWidth = getElementWidth(element)
        return if (elementWidth > iframeWidth) {
            iframeWidth - element.location.getX()
        } else {
            elementWidth
        }
    }

    @CheckReturnValue
    private fun getRescaledElementHeight(element: WebElement, iframeHeight: Int): Int {
        val elementHeight = getElementHeight(element)
        return if (elementHeight > iframeHeight) {
            iframeHeight - element.location.getY()
        } else {
            elementHeight
        }
    }

    @CheckReturnValue
    private fun getRescaledElementWidth(element: WebElement, image: BufferedImage): Int {
        return if (getElementWidth(element) > image.width) {
            image.width - element.location.getX()
        } else {
            getElementWidth(element)
        }
    }

    @CheckReturnValue
    private fun getRescaledElementHeight(element: WebElement, image: BufferedImage): Int {
        return if (getElementHeight(element) > image.height) {
            image.height - element.location.getY()
        } else {
            getElementHeight(element)
        }
    }

    @CheckReturnValue
    private fun getRescaledIframeWidth(iframe: WebElement, image: BufferedImage, driver: WebDriver): Int {
        return if (getIframeWidth(driver) > image.width) {
            image.width - iframe.location.getX()
        } else {
            getIframeWidth(driver)
        }
    }

    @CheckReturnValue
    private fun getIframeWidth(driver: WebDriver): Int {
        return ((driver as JavascriptExecutor).executeScript("return document.body.clientWidth") as Long).toInt()
    }

    @CheckReturnValue
    private fun getElementWidth(element: WebElement): Int {
        return element.size.getWidth()
    }

    @CheckReturnValue
    private fun getElementHeight(element: WebElement): Int {
        return element.size.getHeight()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ScreenShotLaboratory::class.java)
        @JvmStatic
        val instance = ScreenShotLaboratory()
        private val REGEX_PLUS = Pattern.compile("\\+")
        @CheckReturnValue
        private fun isInsideFolder(root: Path, other: Path): Boolean {
            return other.startsWith(root.toAbsolutePath())
        }
    }
}

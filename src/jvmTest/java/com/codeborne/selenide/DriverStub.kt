package com.codeborne.selenide

import okio.ExperimentalFileSystem
import org.mockito.Mockito
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import java.io.File
import java.util.Objects
import java.util.function.Supplier
import javax.annotation.CheckReturnValue
import javax.annotation.Nonnull
import javax.annotation.ParametersAreNonnullByDefault

/**
 * A dummy `Driver` implementation used in tests.
 */
@ParametersAreNonnullByDefault
class DriverStub(
    private val tempDir: Supplier<File>?,
    private val config: Config,
    private val browser: Browser,
    @get:CheckReturnValue override val getAndCheckWebDriver: WebDriver,
    proxy: Any?
) : Driver {

    @JvmOverloads
    constructor(browser: String? = "zopera") : this(
        null, SelenideConfig(), Browser(browser!!, false), Mockito.mock<WebDriver>(
            WebDriver::class.java
        ), null
    ) {
    }

    @CheckReturnValue
    @Nonnull
    override fun config(): Config {
        return config
    }

    @CheckReturnValue
    override fun hasWebDriverStarted(): Boolean {
        return getAndCheckWebDriver != null
    }

    override val webDriver: WebDriver
        get() = getAndCheckWebDriver

    @CheckReturnValue
    @Nonnull
    override fun browser(): Browser {
        return browser
    }

    @ExperimentalFileSystem
    @CheckReturnValue
    override fun browserDownloadsFolder(): DownloadsFolder {
        Objects.requireNonNull(tempDir)
        return SharedDownloadsFolder(tempDir!!.get().toString() + "/build/downloads/45")
    }

    override fun close() {
        getAndCheckWebDriver!!.close()
    }

    @CheckReturnValue
    override fun supportsJavascript(): Boolean {
        return hasWebDriverStarted() && getAndCheckWebDriver is JavascriptExecutor
    }

    override suspend fun <T> executeJavaScript(jsCode: String, vararg arguments: Any?): T {
        return (getAndCheckWebDriver as JavascriptExecutor?)!!.executeScript(jsCode, *arguments) as T
    }

    override suspend fun <T> executeAsyncJavaScript(jsCode: String, vararg arguments: Any?): T {
        return (getAndCheckWebDriver as JavascriptExecutor?)!!.executeAsyncScript(jsCode, *arguments) as T
    }

    override suspend fun getUserAgent(): String = "zhopera"

    @CheckReturnValue
    @Nonnull
    override fun switchTo(): SelenideTargetLocator {
        return SelenideTargetLocator(this)
    }

    @CheckReturnValue
    @Nonnull
    override fun actions(): Actions {
        return Actions(getAndCheckWebDriver)
    }
}

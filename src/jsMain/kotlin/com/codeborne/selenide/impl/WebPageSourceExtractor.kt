package com.codeborne.selenide.impl

import co.touchlab.stately.collections.IsoMutableSet
import com.codeborne.selenide.Config
import okio.ExperimentalFileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import org.lighthousegames.logging.logging
import org.openqa.selenium.UnhandledAlertException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException

open class WebPageSourceExtractor : PageSourceExtractor {
    private val printedErrors: MutableSet<String> = IsoMutableSet()

    @ExperimentalFileSystem
    override suspend fun extract(config: Config, driver: WebDriver, fileName: String): Path {
        return extract(config, driver, fileName, true)
    }

    @ExperimentalFileSystem
    private suspend fun extract(config: Config, driver: WebDriver, fileName: String, retryIfAlert: Boolean): Path {
        val pageSource = createFile(config, fileName)
        try {
            writeToFile(driver.pageSource, pageSource)
        } catch (e: UnhandledAlertException) {
            if (retryIfAlert) {
                retryingExtractionOnAlert(config, driver, fileName, e)
            } else {
                printOnce("savePageSourceToFile", e)
            }
        } catch (e: WebDriverException) {
            log.warn(e) { "Failed to save page source to $fileName" }
            writeToFile(e.toString(), pageSource)
            return pageSource
        } catch (e: RuntimeException) {
            log.error(e) { "Failed to save page source to $fileName" }
            writeToFile(e.toString(), pageSource)
        }
        return pageSource
    }

    @ExperimentalFileSystem
    protected fun createFile(config: Config, fileName: String): Path {
        return FileHelper.canonicalPath(config.reportsFolder().toPath() / "$fileName.html")
    }

    @ExperimentalFileSystem
    protected fun writeToFile(content: String, targetFile: Path) {
        try {
            FileHelper.writeToFile(content.encodeToByteArray(), targetFile)
        } catch (e: IOException) {
            log.error(e) { "Failed to write file ${targetFile}" }
        }
    }

    protected fun printOnce(action: String, error: Throwable) = synchronized(this) {
        if (!printedErrors.contains(action)) {
            log.error(error) { "${error.message}" }
            printedErrors.add(action)
        } else {
            log.error { "Failed to $action: $error" }
        }
    }

    @ExperimentalFileSystem
    private suspend fun retryingExtractionOnAlert(config: Config, driver: WebDriver, fileName: String, e: Exception) {
        try {
            val alert = driver.switchTo().alert()
            log.error { "$e: ${alert.text}" }
            alert.accept()
            extract(config, driver, fileName, false)
        } catch (unableToCloseAlert: Exception) {
            log.error(unableToCloseAlert) { "Failed to close alert" }
        }
    }

    companion object {
        private val log = logging(WebPageSourceExtractor::class.simpleName)
    }
}

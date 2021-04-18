package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import com.codeborne.selenide.impl.FileHelper.copyFile
import org.openqa.selenium.UnhandledAlertException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentSkipListSet
import javax.annotation.CheckReturnValue

open class WebPageSourceExtractor : PageSourceExtractor {
    private val printedErrors: MutableSet<String> = ConcurrentSkipListSet()
    @CheckReturnValue
    override fun extract(config: Config, driver: WebDriver, fileName: String): File {
        return extract(config, driver, fileName, true)
    }

    private fun extract(config: Config, driver: WebDriver, fileName: String, retryIfAlert: Boolean): File {
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
            log.warn("Failed to save page source to {}", fileName, e)
            writeToFile(e.toString(), pageSource)
            return pageSource
        } catch (e: RuntimeException) {
            log.error("Failed to save page source to {}", fileName, e)
            writeToFile(e.toString(), pageSource)
        }
        return pageSource
    }

    protected fun createFile(config: Config, fileName: String): File {
        return File(config.reportsFolder(), "$fileName.html").absoluteFile
    }

    protected fun writeToFile(content: String, targetFile: File) {
        try {
            ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8)).use { `in` -> copyFile(`in`, targetFile) }
        } catch (e: IOException) {
            log.error("Failed to write file {}", targetFile.absolutePath, e)
        }
    }

    @Synchronized
    protected fun printOnce(action: String, error: Throwable) {
        if (!printedErrors.contains(action)) {
            log.error(error.message, error)
            printedErrors.add(action)
        } else {
            log.error("Failed to {}: {}", action, error)
        }
    }

    private fun retryingExtractionOnAlert(config: Config, driver: WebDriver, fileName: String, e: Exception) {
        try {
            val alert = driver.switchTo().alert()
            log.error("{}: {}", e, alert.text)
            alert.accept()
            extract(config, driver, fileName, false)
        } catch (unableToCloseAlert: Exception) {
            log.error("Failed to close alert", unableToCloseAlert)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebPageSourceExtractor::class.java)
    }
}

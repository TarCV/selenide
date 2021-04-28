package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.Stopwatch
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.FileHelper
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.WebElementSource
import okio.ExperimentalFileSystem
import org.openqa.selenium.ElementNotInteractableException
import org.openqa.selenium.WebElement
import okio.IOException
import okio.Path

@ExperimentalFileSystem
class UploadFile : Command<Path> {
    private val describe = Plugins.inject(
        ElementDescriber::class
    )
    @kotlin.time.ExperimentalTime
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): Path {
        checkNotNull(args)
        val file = getFiles(args)
        checkFilesGiven(file)
        checkFilesExist(file)
        val inputField = locator.getWebElement()
        val driver = locator.driver()
        checkValidInputField(driver, inputField)
        val fileNames = listOf(*file).joinToString("\n") { canonicalPath(it) }
        uploadFiles(driver.config(), inputField, fileNames)
        return FileHelper.canonicalPath(file[0])
    }

    private fun checkFilesGiven(file: Array<Path>) {
        require(file.isNotEmpty()) { "No files to upload" }
    }

    private fun checkFilesExist(file: Array<Path>) {
        for (f in file) {
            require(FileHelper.exists(f)) { "Path not found: " + FileHelper.canonicalPath(f) }
        }
    }
    private fun getFiles(args: Array<out Any?>?): Array<Path> {
        val firstOf = Util.firstOf<Any>(args)
        return if (firstOf is Array<*>) {
          firstOf as Array<Path>
        } else {
          args as Array<Path>
        }
    }

    private fun canonicalPath(file: Path): String {
        return try {
            FileHelper.canonicalPath(file).toString()
        } catch (e: IOException) {
            throw IllegalArgumentException("Cannot get canonical path of file $file", e)
        }
    }

    @kotlin.time.ExperimentalTime
    protected suspend fun uploadFiles(config: Config, inputField: WebElement, fileNames: String) {
        val stopwatch = Stopwatch(config.timeout())
        do {
            try {
                inputField.sendKeys(fileNames)
                break
            } catch (notInteractable: ElementNotInteractableException) {
                if (stopwatch.isTimeoutReached) {
                    throw notInteractable
                }
                stopwatch.sleep(config.pollingInterval())
            }
        } while (!stopwatch.isTimeoutReached)
    }

    private suspend fun checkValidInputField(driver: Driver, inputField: WebElement) {
        require("input".equals(inputField.tagName, ignoreCase = true)) {
            "Cannot upload file because " +
                    describe.briefly(driver, inputField) + " is not an INPUT"
        }
    }
}

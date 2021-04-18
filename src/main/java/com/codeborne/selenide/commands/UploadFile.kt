package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.Stopwatch
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.ElementNotInteractableException
import org.openqa.selenium.WebElement
import java.io.File
import java.io.IOException
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class UploadFile : Command<File> {
    private val describe = Plugins.inject(
        ElementDescriber::class.java
    )

    @CheckReturnValue
    @Throws(IOException::class)
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>?): File {
        checkNotNull(args)
        val file = getFiles(args)
        checkFilesGiven(file)
        checkFilesExist(file)
        val inputField = locator.webElement
        val driver = locator.driver()
        checkValidInputField(driver, inputField)
        val fileNames = Stream.of(*file).map { file: File -> canonicalPath(file) }
            .collect(Collectors.joining("\n"))
        uploadFiles(driver.config(), inputField, fileNames)
        return file[0].canonicalFile
    }

    private fun checkFilesGiven(file: Array<File>) {
        require(file.isNotEmpty()) { "No files to upload" }
    }

    private fun checkFilesExist(file: Array<File>) {
        for (f in file) {
            require(f.exists()) { "File not found: " + f.absolutePath }
        }
    }

    @CheckReturnValue
    private fun getFiles(args: Array<out Any?>?): Array<File> {
        val firstOf = Util.firstOf<Any>(args)
        return if (firstOf is Array<*>) {
          firstOf as Array<File>
        } else {
          args as Array<File>
        }
    }

    private fun canonicalPath(file: File): String {
        return try {
            file.canonicalPath
        } catch (e: IOException) {
            throw IllegalArgumentException("Cannot get canonical path of file $file", e)
        }
    }

    protected fun uploadFiles(config: Config, inputField: WebElement, fileNames: String?) {
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

    private fun checkValidInputField(driver: Driver, inputField: WebElement) {
        require("input".equals(inputField.tagName, ignoreCase = true)) {
            "Cannot upload file because " +
                    describe.briefly(driver, inputField) + " is not an INPUT"
        }
    }
}

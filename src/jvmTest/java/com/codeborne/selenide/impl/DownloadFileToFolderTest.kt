package com.codeborne.selenide.impl

import com.codeborne.selenide.Browser
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.SelenideConfig
import com.codeborne.selenide.files.FileFilters.none
import com.codeborne.selenide.impl.DownloadFileToFolder.Companion.isFileModifiedLaterThan
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import okio.ExperimentalFileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import org.apache.commons.io.FileUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.function.Supplier

@ExperimentalFileSystem
internal class DownloadFileToFolderTest {
    private val downloader = Downloader()
    private val waiter: Waiter = DummyWaiter()
    private val windowsCloser: WindowsCloser = Mockito.spy(DummyWindowsCloser())
    private val command = DownloadFileToFolder(downloader, waiter, windowsCloser)
    private val config = SelenideConfig()
    private val webdriver = Mockito.mock(WebDriver::class.java)
    private val linkWithHref = Mockito.mock(WebElementSource::class.java)
    private val link = Mockito.mock(WebElement::class.java)

    @TempDir
    @JvmField
    var tempDir: File? = null

    private val driver by lazy {
        DriverStub({ tempDir!! }, config, Browser("opera", false), webdriver, null)
    }

    @BeforeEach
    fun setUp() = runBlockingTest {
        Mockito.`when`(linkWithHref.driver()).thenReturn(driver)
        Mockito.`when`<Any>(linkWithHref.findAndAssertElementIsInteractable()).thenReturn(link)
        Mockito.`when`(linkWithHref.toString()).thenReturn("<a href='report.pdf'>report</a>")
    }

    @Test
    @Throws(IOException::class)
    fun tracksForNewFilesInDownloadsFolder() = runBlockingTest {
        val newFileName = "bingo-bongo.txt"
        Mockito.doAnswer {
            FileUtils.writeStringToFile(
                driver.browserDownloadsFolder().file(newFileName).toFile(),
                "Hello Bingo-Bongo",
                StandardCharsets.UTF_8
            )
            null
        }.`when`(link).click()
        val downloadedFile = command.download(linkWithHref, link, 3000, none())
        Assertions.assertThat(downloadedFile.name).isEqualTo(newFileName)
        Assertions.assertThat(downloadedFile.parent).isNotEqualTo(driver.browserDownloadsFolder())
        Assertions.assertThat(FileUtils.readFileToString(downloadedFile.toFile(), StandardCharsets.UTF_8))
            .isEqualTo("Hello Bingo-Bongo")
    }

    @Test
    @Throws(IOException::class)
    fun fileModificationCheck() {
        Assertions.assertThat(isFileModifiedLaterThan(file(1597333000L), 1597333000L)).isTrue
        Assertions.assertThat(isFileModifiedLaterThan(file(1597333000L), 1597332999L)).isTrue
        Assertions.assertThat(isFileModifiedLaterThan(file(1597333000L), 1597334000L)).isFalse
    }

    @Test
    @Throws(IOException::class)
    fun fileModificationCheck_workWithSecondsPrecision() {
        Assertions.assertThat(isFileModifiedLaterThan(file(1111111000L), 1111111000L)).isTrue
        Assertions.assertThat(isFileModifiedLaterThan(file(1111111000L), 1111111999L)).isTrue
        Assertions.assertThat(isFileModifiedLaterThan(file(1111111000L), 1111112000L)).isFalse
    }

    @Throws(IOException::class)
    private fun file(modifiedAt: Long): Path {
        val file = File.createTempFile("selenide-tests", "new-file")
        FileUtils.touch(file)
        file.setLastModified(modifiedAt)
        return file.toOkioPath()
    }
}

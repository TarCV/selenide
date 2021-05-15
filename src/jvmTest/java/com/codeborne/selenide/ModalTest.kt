package com.codeborne.selenide

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasClass
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import com.codeborne.selenide.ex.DialogTextMismatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.apache.commons.io.IOUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.Alert
import org.openqa.selenium.OutputType
import org.openqa.selenium.chrome.ChromeDriver
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
internal class ModalTest {
    private val alert = Mockito.mock(Alert::class.java)
    private val webDriver = Mockito.mock(ChromeDriver::class.java, Mockito.RETURNS_DEEP_STUBS)
    private val config = SelenideConfig()
    private val driver: Driver = DriverStub(null, config, Browser("chrome", false), webDriver, null)
    private var reportsBaseUri: URI? = null
    @BeforeEach
    @Throws(IOException::class)
    fun setUp() {
        Mockito.`when`(webDriver.switchTo().alert()).thenReturn(alert)
        Mockito.`when`(alert.text).thenReturn(ALERT_TEXT)
        config.reportsFolder("build/reports/tests/ModalTest")
        Mockito.`when`(webDriver.pageSource).thenReturn("<html/>")
        Mockito.`when`(webDriver.getScreenshotAs(OutputType.BYTES))
            .thenReturn(IOUtils.resourceToByteArray("/screenshot.png"))
        reportsBaseUri = File(System.getProperty("user.dir"), config.reportsFolder()).toURI()
    }

    @Test
    fun confirmAcceptsDialogAndReturnsText() = runBlockingTest {
        val text = Modal(driver).confirm()
        Mockito.verify(alert).accept()
        Assertions.assertThat(text).isEqualTo(ALERT_TEXT)
    }

    @Test
    fun confirmWithExpectedTextAcceptsDialogAndReturnsText() = runBlockingTest {
        val text = Modal(driver).confirm(ALERT_TEXT)
        Mockito.verify(alert).accept()
        Assertions.assertThat(text).isEqualTo(ALERT_TEXT)
    }

    @Test
    fun confirmWithIncorrectExpectedTextAcceptsDialogAndThrowsException() = runBlockingTest {
        assertThat { Modal(driver).confirm("Are you sure?") }
            .isFailure()
            .all {
                isInstanceOf(DialogTextMismatch::class)
                message().isNotNull().all {
                    contains(String.format("Actual: %s%nExpected: Are you sure?%n", ALERT_TEXT))
                    contains("Screenshot: $reportsBaseUri")
                }
            }
        Mockito.verify(alert).accept()
    }

    @Test
    fun promptAcceptsDialogAndReturnsText() = runBlockingTest {
        val text = Modal(driver).prompt()
        Mockito.verify(alert).accept()
        Assertions.assertThat(text).isEqualTo(ALERT_TEXT)
    }

    @Test
    fun promptWithInputAcceptsDialogAndReturnsText() = runBlockingTest {
        val text = Modal(driver).prompt("Sure do")
        Mockito.verify(alert).sendKeys("Sure do")
        Mockito.verify(alert).accept()
        Assertions.assertThat(text).isEqualTo(ALERT_TEXT)
    }

    @Test
    fun promptWithExpectedTextAndInputAcceptsDialogAndReturnsText() = runBlockingTest {
        val text = Modal(driver).prompt(ALERT_TEXT, "Sure do")
        Mockito.verify(alert).sendKeys("Sure do")
        Mockito.verify(alert).accept()
        Assertions.assertThat(text).isEqualTo(ALERT_TEXT)
    }

    @Test
    fun promptWithIncorrectExpectedTextAndInputAcceptsDialogAndReturnsText() = runBlockingTest {
        assertThat {
            Modal(driver).prompt("Are you sure?", "Sure do")
        }
            .isFailure()
            .all {
                 isInstanceOf(DialogTextMismatch::class)
                message().isNotNull().all {
                    contains(String.format("Actual: %s%nExpected: Are you sure?%n", ALERT_TEXT))
                    contains("Screenshot: " + convertFilePath(System.getProperty("user.dir") + "/" + config.reportsFolder() + "/"))
                }
            }
        Mockito.verify(alert).sendKeys("Sure do")
        Mockito.verify(alert).accept()
    }

    @Test
    fun dismissDismissesDialogAndReturnsText() = runBlockingTest {
        val text = Modal(driver).dismiss()
        Mockito.verify(alert).dismiss()
        Assertions.assertThat(text).isEqualTo(ALERT_TEXT)
    }

    @Test
    fun dismissWithExpectedTextAcceptsDialogAndReturnsText() = runBlockingTest {
        val text = Modal(driver).dismiss(ALERT_TEXT)
        Mockito.verify(alert).dismiss()
        Assertions.assertThat(text).isEqualTo(ALERT_TEXT)
    }

    @Test
    fun dismissWithIncorrectExpectedTextAcceptsDialogAndThrowsException() = runBlockingTest {
        val exception = assertThat {
            Modal(driver).dismiss("Are you sure?")
        }
            .isFailure()
            .all {
                isInstanceOf(DialogTextMismatch::class)
                message().isNotNull().all {
                    contains(String.format("Actual: %s%nExpected: Are you sure?%n", ALERT_TEXT))
                    contains("Screenshot: " + convertFilePath(System.getProperty("user.dir") + "/" + config.reportsFolder() + "/"))
                }
            }
        Mockito.verify(alert).dismiss()
    }

    private fun convertFilePath(path: String): String {
        return try {
            File(path).toURI().toURL().toExternalForm()
        } catch (e: MalformedURLException) {
            "file://$path"
        }
    }

    companion object {
        private const val ALERT_TEXT = "You really want it?"
    }
}

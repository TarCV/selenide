package com.codeborne.selenide.commands

import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isSuccess
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito
import org.openqa.selenium.InvalidSelectorException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import java.io.File

@ExperimentalCoroutinesApi
internal class ExistsCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val element = Mockito.mock(WebElement::class.java)
    private val existsCommand = Exists()

    @TempDir
    @JvmField
    var tempDir: File? = null

    @Test
    fun testExistExecuteMethod() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(element)
        assertThat(existsCommand.executeBlocking(proxy, locator, arrayOf()))
            .isTrue
    }

    @Test
    fun testExistExecuteMethodWithWebDriverException() = runBlockingTest {
        checkExecuteMethodWithException(WebDriverException())
    }

    private suspend fun <T : Throwable?> checkExecuteMethodWithException(exception: T) {
        assertk.assertThat { executeMethodWithException(exception) }
            .isSuccess()
            .isFalse()
    }

    private suspend fun <T : Throwable?> executeMethodWithException(exception: T): Boolean {
        Mockito.doThrow(exception).`when`(locator).getWebElement()
        return existsCommand.execute(proxy, locator, arrayOf())
    }

    @Test
    fun testExistExecuteMethodElementNotFoundException() = runBlockingTest {
        val driver: Driver = DriverStub()
        checkExecuteMethodWithException(ElementNotFound(driver, "", Condition.appear))
    }

    @Test
    fun testExistsExecuteMethodInvalidSelectorException() = runBlockingTest {
        assertk.assertThat { executeMethodWithException(InvalidSelectorException("Element is not selectable")) }
            .isFailure()
            .isInstanceOf(InvalidSelectorException::class.java)
    }

    @Test
    fun testExistsExecuteMethodWithIndexOutOfBoundException() = runBlockingTest {
        checkExecuteMethodWithException(IndexOutOfBoundsException("Out of bound"))
    }
}

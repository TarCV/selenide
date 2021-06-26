package com.codeborne.selenide.commands

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.doThrow
import org.openqa.selenium.By
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class ToStringCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val driver: Driver = DriverStub()
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedFoundElement = Mockito.mock(WebElement::class.java)
    private val toStringCommand = ToString()
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`(locator.driver()).thenReturn(driver)
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedFoundElement)
    }

    @Test
    fun executeMethod() = runBlockingTest {
        Mockito.`when`(mockedFoundElement.isSelected).thenReturn(true)
        Mockito.`when`(mockedFoundElement.isDisplayed).thenReturn(true)
        val elementText = "text"
        Mockito.`when`(mockedFoundElement.text).thenReturn(elementText)
        val elementString = toStringCommand.execute(proxy, locator, arrayOf())
        assertThat(elementString)
            .isEqualTo("<null selected:true>text</null>")
    }

    @Test
    fun executeMethodWhenWebDriverExceptionIsThrown() = runBlockingTest {
        Mockito.doThrow(WebDriverException()).`when`(locator).getWebElement()
        val elementString = toStringCommand.execute(proxy, locator, arrayOf())
        assertThat(elementString)
            .contains("WebDriverException")
    }

    @Test
    fun executeMethodWhenElementNotFoundIsThrown() = runBlockingTest {
        doThrow(ElementNotFound(driver, By.name(""), Condition.visible)).`when`<WebElementSource>(locator)
            .getWebElement()
        val elementString = toStringCommand.execute(proxy, locator, arrayOf())
        assertThat(elementString)
            .isEqualTo(
                String.format(
                    "Element not found {By.name: }\n" +
                            "Expected: visible\n" +
                            "Timeout: 0 ms."
                )
            )
    }

    @Test
    fun executeMethodWhenIndexOutOfBoundExceptionIsThrown() = runBlockingTest {
        Mockito.doThrow(IndexOutOfBoundsException()).`when`(locator).getWebElement()
        val elementString = toStringCommand.execute(proxy, locator, arrayOf())
        assertThat(elementString)
            .isEqualTo("java.lang.IndexOutOfBoundsException")
    }
}

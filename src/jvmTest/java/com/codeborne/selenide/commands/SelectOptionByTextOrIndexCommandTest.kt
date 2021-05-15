package com.codeborne.selenide.commands

import assertk.all
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.message
import assertk.assertions.startsWith
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
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Quotes

@ExperimentalCoroutinesApi
internal class SelectOptionByTextOrIndexCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val selectField = Mockito.mock(WebElementSource::class.java)
    private val selectOptionByTextOrIndexCommand = SelectOptionByTextOrIndex()
    private val mockedElement = Mockito.mock(WebElement::class.java)
    private val defaultElementText = "This is element text"
    private val mockedFoundElement = Mockito.mock(WebElement::class.java)
    private val defaultIndex = 1
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`(selectField.driver()).thenReturn(DriverStub())
        Mockito.`when`<Any>(selectField.getWebElement()).thenReturn(mockedElement)
        Mockito.`when`(mockedElement.text).thenReturn(defaultElementText)
        Mockito.`when`(mockedElement.tagName).thenReturn("select")
        Mockito.`when`(mockedFoundElement.isSelected).thenReturn(true)
    }

    @Test
    fun testSelectOptionByText() = runBlockingTest {
        Mockito.`when`(
            mockedElement.findElements(
                By.xpath(
                    ".//option[normalize-space(.) = " + Quotes.escape(
                        defaultElementText
                    ) + "]"
                )
            )
        )
            .thenReturn(listOf(mockedFoundElement))
        selectOptionByTextOrIndexCommand.execute(proxy, selectField, arrayOf<Any>(arrayOf(defaultElementText)))
    }

    @Test
    fun selectOptionByTextWhenElementIsNotFound() = runBlockingTest {
        try {
            selectOptionByTextOrIndexCommand.execute(proxy, selectField, arrayOf<Any>(arrayOf(defaultElementText)))
        } catch (exception: ElementNotFound) {
            assertk.assertThat(exception)
                .message()
                .isNotNull()
                .all {
                    startsWith(
                        String.format(
                            "Element not found {null/option[text:%s]}%nExpected: exist",
                            defaultElementText
                        )
                    )
                }
        }
    }

    @Test
    fun selectedOptionByTextWhenNoSuchElementIsThrown() = runBlockingTest {
        Mockito.doThrow(NoSuchElementException("no element found"))
            .`when`(mockedElement)
            .findElements(By.xpath(".//option[normalize-space(.) = " + Quotes.escape(defaultElementText) + "]"))
        try {
            selectOptionByTextOrIndexCommand.execute(proxy, selectField, arrayOf<Any>(arrayOf("")))
        } catch (exception: ElementNotFound) {
            assertThat<ElementNotFound>(exception)
                .withFailMessage(
                    String.format(
                        "Element not found {null/option[text:]}%nExpected: exist%n" +
                                "Timeout: 0 ms.%n" +
                                "Caused by: NoSuchElementException: Cannot locate element with text:"
                    )
                )
        }
    }

    @Test
    fun selectOptionByIndex() = runBlockingTest {
        Mockito.`when`(mockedElement.findElements(By.tagName("option"))).thenReturn(listOf(mockedFoundElement))
        Mockito.`when`(mockedFoundElement.getAttribute("index")).thenReturn(defaultIndex.toString())
        selectOptionByTextOrIndexCommand.execute(proxy, selectField, arrayOf<Any>(intArrayOf(defaultIndex)))
    }

    @Test
    fun selectOptionByIndexWhenNoElementFound() = runBlockingTest {
        try {
            selectOptionByTextOrIndexCommand.execute(proxy, selectField, arrayOf<Any>(intArrayOf(defaultIndex)))
        } catch (exception: ElementNotFound) {
            assertk.assertThat(exception)
                .message()
                .isNotNull()
                .all {
                    startsWith(
                        String.format(
                            "Element not found {null/option[index:%d]}%nExpected: exist",
                            defaultIndex
                        )
                    )
                }
        }
    }

    @Test
    fun executeMethodWhenArgIsNotStringOrInt() = runBlockingTest {
        assertk.assertThat(selectOptionByTextOrIndexCommand.execute(proxy, selectField, arrayOf<Any>(arrayOf(1.0))))
            .isNull()
    }
}

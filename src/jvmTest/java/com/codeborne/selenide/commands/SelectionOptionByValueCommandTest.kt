package com.codeborne.selenide.commands

import assertk.all
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
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
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class SelectionOptionByValueCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val selectField = Mockito.mock(WebElementSource::class.java)
    private val selectOptionByValueCommand = SelectOptionByValue()
    private val element = Mockito.mock(WebElement::class.java)
    private val foundElement = Mockito.mock(WebElement::class.java)
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`(selectField.driver()).thenReturn(DriverStub())
        Mockito.`when`<Any>(selectField.getWebElement()).thenReturn(element)
        Mockito.`when`<Any>(selectField.description()).thenReturn("By.tagName{select}")
        Mockito.`when`(element.text).thenReturn("walue")
        Mockito.`when`(element.tagName).thenReturn("select")
        Mockito.`when`(foundElement.isSelected).thenReturn(true)
    }

    @Test
    fun selectByValueWithStringFromArgs() = runBlockingTest {
        Mockito.`when`(element.findElements(By.xpath(".//option[@value = \"walue\"]")))
            .thenReturn(listOf(foundElement))
        selectOptionByValueCommand.execute(proxy, selectField, arrayOf<Any>("walue"))
    }

    @Test
    fun selectByValueWithStringArrayFromArgs() = runBlockingTest {
        Mockito.`when`(element.findElements(By.xpath(".//option[@value = \"walue\"]")))
            .thenReturn(listOf(foundElement))
        selectOptionByValueCommand.execute(proxy, selectField, arrayOf<Any>(arrayOf("walue")))
    }

    @Test
    fun selectByValueWithArgNotString() = runBlockingTest {
        assertk.assertThat(selectOptionByValueCommand.execute(proxy, selectField, arrayOf<Any>(intArrayOf(1))))
            .isInstanceOf(Unit::class)
    }

    @Test
    fun selectByValueWhenElementIsNotFound() = runBlockingTest {
        Mockito.`when`(element.findElements(By.xpath(".//option[@value = \"walue\"]")))
            .thenReturn(emptyList())
        assertk.assertThat { selectOptionByValueCommand.execute(proxy, selectField, arrayOf<Any>(arrayOf("walue"))) }
            .isFailure()
            .isInstanceOf(ElementNotFound::class.java)
            .message()
            .isNotNull()
            .all {
                startsWith(String.format("Element not found {By.tagName{select}/option[value:walue]}\nExpected: exist"))
            }
    }
}

package com.codeborne.selenide.commands

import assertk.assertions.hasMessage
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.Mocks.mockElement
import com.codeborne.selenide.Mocks.mockWebElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
internal class FindAllByXpathCommandTest : WithAssertions {
    private val parentWebElement = mockWebElement("div", "I am parent")
    private val parentSelenideElement = mockElement("div", "I am parent")
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val webElement = mockWebElement("div", "Default Text")
    private val selenideElement = mockElement("div", "Default Text")
    private val findAllByXpathCommand = FindAllByXpath()
    @BeforeEach
    fun setup() {
        Mockito.`when`<Any>(parentSelenideElement.toWebElement()).thenReturn(parentWebElement)
        Mockito.`when`<Any>(selenideElement.toWebElement()).thenReturn(webElement)
        Mockito.`when`(selenideElement.isSelected).thenReturn(true)
        Mockito.`when`(locator.driver()).thenReturn(DriverStub())
    }

    @Test
    fun executeMethodWithNoArgsPassed() = runBlockingTest {
        assertk.assertThat { findAllByXpathCommand.execute(parentSelenideElement, locator, arrayOf()) }
            .isFailure()
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Missing arguments")
    }

    @Test
    fun executeMethodWithZeroLengthArgs() = runBlockingTest {
        Mockito.`when`(parentWebElement.findElement(ArgumentMatchers.any())).thenReturn(webElement)
        val findAllCommandCollection = findAllByXpathCommand.execute(parentSelenideElement, locator, arrayOf("."))
        assertThat(findAllCommandCollection.first().text).isEqualTo("Default Text")
        Mockito.verify(parentWebElement).findElement(By.xpath("."))
        Mockito.verify(parentSelenideElement, Mockito.never()).findElements(ArgumentMatchers.any())
    }

    @Test
    fun executeMethodWithMoreThenOneArgsList() = runBlockingTest {
        Mockito.`when`(parentWebElement.findElement(ArgumentMatchers.any())).thenReturn(webElement)
        val findAllCommandCollection =
            findAllByXpathCommand.execute(parentSelenideElement, locator, arrayOf(".", "/.."))
        assertThat(findAllCommandCollection.first().text).isEqualTo("Default Text")
        Mockito.verify(parentWebElement).findElement(By.xpath("."))
        Mockito.verify(parentSelenideElement, Mockito.never()).findElements(ArgumentMatchers.any())
    }
}

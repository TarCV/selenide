package com.codeborne.selenide.commands

import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.InvalidSelectorException
import org.openqa.selenium.NotFoundException
import org.openqa.selenium.WebDriverException

@ExperimentalCoroutinesApi
internal class MatchesCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val driver = Mockito.mock(Driver::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val matchesCommand = Matches()
    @Test
    fun testExecuteMethodWhenNoElementFound() = runBlockingTest {
        Mockito.`when`<Any?>(locator.getWebElement()).thenReturn(null)
        assertk.assertThat(matchesCommand.execute(proxy, locator, arrayOf<Any>(Condition.disabled)))
            .isFalse()
    }

    @Test
    fun testExecuteMethodWhenElementDoesntMeetCondition() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
        Mockito.`when`(locator.driver()).thenReturn(driver)
        Mockito.`when`(mockedElement.isEnabled).thenReturn(true)
        assertk.assertThat(matchesCommand.execute(proxy, locator, arrayOf<Any>(Condition.disabled)))
            .isFalse()
    }

    @Test
    fun testExecuteMethodWhenElementMeetsCondition() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
        Mockito.`when`(locator.driver()).thenReturn(driver)
        Mockito.`when`(mockedElement.isEnabled).thenReturn(true)
        assertk.assertThat(matchesCommand.execute(proxy, locator, arrayOf<Any>(Condition.enabled)))
            .isTrue()
    }

    @Test
    fun testExecuteMethodWhenWebDriverExceptionIsThrown() {
        catchExecuteMethodWithException(WebDriverException())
    }

    private fun <T : Throwable?> catchExecuteMethodWithException(exception: T) = runBlockingTest {
        Mockito.doThrow(exception).`when`(locator).getWebElement()
        assertk.assertThat(matchesCommand.execute(proxy, locator, arrayOf<Any>(Condition.enabled)))
            .isFalse()
    }

    @Test
    fun testExecuteMethodWhenNotFoundExceptionIsThrown() {
        catchExecuteMethodWithException(NotFoundException())
    }

    @Test
    fun testExecuteMethodWhenIndexOutOfBoundsExceptionIsThrown() {
        catchExecuteMethodWithException(IndexOutOfBoundsException())
    }

    @Test
    fun testExecuteMethodWhenExceptionWithInvalidSelectorException() {
        assertThatThrownBy { catchExecuteMethodWithException(NotFoundException("invalid selector")) }
            .isInstanceOf(InvalidSelectorException::class.java)
    }

    @Test
    fun testExecuteMethodWhenRunTimeExceptionIsThrown() {
        assertThatThrownBy { catchExecuteMethodWithException(RuntimeException("invalid selector")) }
            .isInstanceOf(InvalidSelectorException::class.java)
    }
}

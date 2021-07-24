package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.lighthousegames.logging.KmLog
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.same
import org.mockito.kotlin.spy
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class EventsTest {
    private val log = Mockito.mock(KmLog::class.java)
    private val element = Mockito.mock(WebElement::class.java)
    private val driver = Mockito.mock(Driver::class.java)
    @Test
    fun triggersEventsByExecutingJSCode() = runBlockingTest {
        val events = spy(Events(log)) {
            onBlocking { executeJavaScript(any(), same(element), any()) } doReturn Unit
        }

        events.fireEvent(driver, element, "input", "keyup", "change")
        Mockito.verify(events).executeJavaScript(driver, element, "input", "keyup", "change")
        Mockito.verifyNoMoreInteractions(log)
    }

    @Test
    fun ignoresStaleElementReferenceException() = runBlockingTest {
        val events = spy(Events(log)) {
            onBlocking { executeJavaScript(any(), same(element), any()) } doThrow StaleElementReferenceException::class
        }

        events.fireEvent(driver, element, "change")
        Mockito.verify(events).executeJavaScript(driver, element, "change")
        Mockito.verifyNoMoreInteractions(log)
    }

    @Test
    fun ignoresButLogs_anyOtherExceptions() = runBlockingTest {
        val events = spy(Events(log)) {
            onBlocking { executeJavaScript(any(), same(element), any()) } doThrow
                UnsupportedOperationException("webdriver does not support JS")
        }

        events.fireEvent(driver, element, "input", "change")
        Mockito.verify(events).executeJavaScript(driver, element, "input", "change")
        Mockito.verify(log).warn(
            UnsupportedOperationException("webdriver does not support JS"),
            null
        ) { "Failed to trigger events input: change" }
    }
}

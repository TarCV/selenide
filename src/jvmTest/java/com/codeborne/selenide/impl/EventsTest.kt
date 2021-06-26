package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import org.lighthousegames.logging.KmLog
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.same
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class EventsTest {
    private val log = Mockito.mock(KmLog::class.java)
    private val events = Mockito.spy(Events(log))
    private val element = Mockito.mock(WebElement::class.java)
    private val driver = Mockito.mock(Driver::class.java)
    @Test
    fun triggersEventsByExecutingJSCode() = runBlockingTest {
        Mockito.doNothing().`when`(events)
            .executeJavaScript(any(), same(element), any())
        events.fireEvent(driver, element, "input", "keyup", "change")
        Mockito.verify(events).executeJavaScript(driver, element, "input", "keyup", "change")
        Mockito.verifyNoMoreInteractions(log)
    }

    @Test
    fun ignoresStaleElementReferenceException() = runBlockingTest {
        Mockito.doThrow(StaleElementReferenceException::class.java).`when`(events)
            .executeJavaScript(any(), same(element), any())
        events.fireEvent(driver, element, "change")
        Mockito.verify(events).executeJavaScript(driver, element, "change")
        Mockito.verifyNoMoreInteractions(log)
    }

    @Test
    fun ignoresButLogs_anyOtherExceptions() = runBlockingTest {
        Mockito.doThrow(UnsupportedOperationException("webdriver does not support JS"))
            .`when`(events)
            .executeJavaScript(any(), same(element), any())
        events.fireEvent(driver, element, "input", "change")
        Mockito.verify(events).executeJavaScript(driver, element, "input", "change")
        Mockito.verify(log).warn(
            UnsupportedOperationException("webdriver does not support JS"),
            null
        ) { "Failed to trigger events input: change" }
    }
}

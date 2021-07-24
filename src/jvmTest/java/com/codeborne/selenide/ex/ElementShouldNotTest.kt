package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.DriverStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class ElementShouldNotTest : WithAssertions {
    private val driver: Driver = DriverStub()
    @Test
    fun testToString() = runBlockingTest {
        val element = Mockito.mock(WebElement::class.java)
        whenever(element.text).thenReturn("text")
        whenever(element.tagName).thenReturn("tag")
        val elementShould = ElementShouldNot.ElementShouldNot(
            driver, "by.name: selenide", "be ", Condition.appear,
            element, Throwable("Error message")
        )
        assertThat(elementShould).hasMessage(
            "Element should not be visible {by.name: selenide}\n" +
                    "Element: '<tag displayed:false>text</tag>'\n" +
                    "Actual value: visible:false\n" +
                    "Timeout: 0 ms.\n" +
                    "Caused by: java.lang.Throwable: Error message"
        )
    }
}

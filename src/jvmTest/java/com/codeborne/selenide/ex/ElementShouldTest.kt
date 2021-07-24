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
internal class ElementShouldTest : WithAssertions {
    @Test
    fun testToString() = runBlockingTest {
        val searchCriteria = "by.name: selenide"
        val prefix = "be "
        val driver: Driver = DriverStub()
        val webElementMock = Mockito.mock(WebElement::class.java)
        whenever(webElementMock.text).thenReturn("")
        whenever(webElementMock.tagName).thenReturn("tag")
        val exception = Exception("Error message")
        val elementShould = ElementShould.ElementShould(driver, searchCriteria, prefix, Condition.appear, webElementMock, exception)
        assertThat(elementShould)
            .hasMessage(
                "Element should be visible {by.name: selenide}\n" +
                        "Element: '<tag displayed:false></tag>'\n" +
                        "Actual value: visible:false\n" +
                        "Timeout: 0 ms.\n" +
                        "Caused by: java.lang.Exception: Error message"
            )
    }
}

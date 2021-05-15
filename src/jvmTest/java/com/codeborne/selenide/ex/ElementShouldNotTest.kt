package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.DriverStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class ElementShouldNotTest : WithAssertions {
    private val driver: Driver = DriverStub()
    @Test
    fun testToString() = runBlockingTest {
        val elementShould = ElementShouldNot.ElementShouldNot(
            driver, "by.name: selenide", "be ", Condition.appear,
            Mockito.mock(WebElement::class.java), Throwable("Error message")
        )
        assertThat(elementShould).hasMessage(
            "Element should not be visible {by.name: selenide}" + System.lineSeparator() +
                    "Element: '<null displayed:false></null>'" + System.lineSeparator() +
                    "Actual value: visible:false" + System.lineSeparator() +
                    "Timeout: 0 ms." + System.lineSeparator() +
                    "Caused by: java.lang.Throwable: Error message"
        )
    }
}

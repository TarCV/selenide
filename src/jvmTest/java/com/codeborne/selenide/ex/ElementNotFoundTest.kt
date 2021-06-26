package com.codeborne.selenide.ex

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.collections.ExactTexts
import com.codeborne.selenide.ex.ElementNotFound.Companion.ElementNotFound
import com.codeborne.selenide.impl.CollectionSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.By
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class ElementNotFoundTest : WithAssertions {
    private val driver: Driver = DriverStub()
    @Test
    fun elementNotFoundWithByCriteria() {
        val elementNotFoundById = ElementNotFound(driver, By.id("Hello"), Condition.exist)
        val expectedMessage = String.format(
            "Element not found {By.id: Hello}\n" +
                    "Expected: exist\n" +
                    "Timeout: 0 ms."
        )
        assertThat(elementNotFoundById).hasMessage(expectedMessage)
    }

    @Test
    fun elementNotFoundWithStringCriteria() {
        val elementNotFoundById = ElementNotFound(driver, "Hello", Condition.exist)
        val expectedMessage = String.format(
            "Element not found {Hello}\n" +
                    "Expected: exist\n" +
                    "Timeout: 0 ms."
        )
        assertThat(elementNotFoundById).hasMessage(expectedMessage)
    }

    @Test
    fun elementNotFoundWithStringCriteriaAndThrowableError() {
        val elementNotFoundById = ElementNotFound(driver, "Hello", Condition.exist, Throwable("Error message"))
        val expectedMessage = String.format(
            "Element not found {Hello}\n" +
                    "Expected: exist\n" +
                    "Timeout: 0 ms.\n" +
                    "Caused by: java.lang.Throwable: Error message"
        )
        assertThat(elementNotFoundById).hasMessage(expectedMessage)
    }

    @Test
    fun elementNotFoundWithWebElementCollectionAndThrowableError() = runBlockingTest {
        val webElementCollectionMock = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`(webElementCollectionMock.driver()).thenReturn(driver)
        Mockito.`when`<Any>(webElementCollectionMock.description()).thenReturn("mock collection description")
        val expectedStrings = listOf("One", "Two", "Three")
        val elementNotFoundById = ElementNotFound(
            webElementCollectionMock,
            ExactTexts(expectedStrings).toString(),
            Throwable("Error message")
        )
        val expectedMessage = String.format(
            "Element not found {mock collection description}\n" +
                    "Expected: Exact texts [One, Two, Three]\n" +
                    "Timeout: 0 ms.\n" +
                    "Caused by: java.lang.Throwable: Error message"
        )
        assertThat(elementNotFoundById).hasMessage(expectedMessage)
    }
}

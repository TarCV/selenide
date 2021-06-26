package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.Mocks.mockElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class ListSizeMismatchTest : WithAssertions {
    private val expectedSize = 10
    private val driver: Driver = DriverStub()
    private val collection = mockCollection("Collection description")
    private val actualElementsList: List<WebElement> =
        Arrays.asList(mockElement("Niff"), mockElement("Naff"), mockElement("Nuff"))
    private val exception = Exception("Something happened")
    private val timeoutMs = 1000L
    @BeforeEach
    fun setUp() {
        Mockito.`when`(collection.driver()).thenReturn(driver)
    }

    @Test
    fun toString_withoutExplanation() = runBlockingTest {
        val listSizeMismatch = ListSizeMismatch(
            driver, "<=",
            expectedSize,
            null,
            collection,
            actualElementsList,
            exception,
            timeoutMs
        )
        assertThat(listSizeMismatch)
            .hasMessage(
                String.format(
                    "List size mismatch: expected: <= 10, actual: 3, collection: Collection description\n" +
                            "Elements: [\n" +
                            "\t<div displayed:false>Niff</div>,\n" +
                            "\t<div displayed:false>Naff</div>,\n" +
                            "\t<div displayed:false>Nuff</div>\n" +
                            "]\n" +
                            "Timeout: 1 s.\n" +
                            "Caused by: java.lang.Exception: Something happened"
                )
            )
    }

    @Test
    fun toString_withExplanation() = runBlockingTest {
        val listSizeMismatch = ListSizeMismatch(
            driver, ">",
            expectedSize,
            "it's said in customer requirement #12345",
            collection,
            actualElementsList,
            exception,
            timeoutMs
        )
        assertThat(listSizeMismatch)
            .hasMessage(
                String.format(
                    "List size mismatch: expected: > 10" +
                            " (because it's said in customer requirement #12345), actual: 3, collection: Collection description\n" +
                            "Elements: [\n" +
                            "\t<div displayed:false>Niff</div>,\n" +
                            "\t<div displayed:false>Naff</div>,\n" +
                            "\t<div displayed:false>Nuff</div>\n" +
                            "]\n" +
                            "Timeout: 1 s.\n" +
                            "Caused by: java.lang.Exception: Something happened"
                )
            )
    }
}

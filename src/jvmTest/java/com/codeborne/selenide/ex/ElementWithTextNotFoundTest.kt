package com.codeborne.selenide.ex

import com.codeborne.selenide.Mocks.mockCollection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.openqa.selenium.NoSuchElementException
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class ElementWithTextNotFoundTest : WithAssertions {
    private val actualTexts = Arrays.asList("Niff", "Naff", "Nuff")
    private val expectedTexts = Arrays.asList("Piff", "Paff", "Puff")
    private val cause: Throwable = NoSuchElementException("ups")
    @Test
    fun errorMessage() = runBlockingTest {
        val elementWithTextNotFound = ElementWithTextNotFound.ElementWithTextNotFound(
            mockCollection(".characters"),
            actualTexts,
            expectedTexts,
            null, 9000, cause
        )
        assertThat(elementWithTextNotFound).hasMessage(
            String.format(
                "Element with text not found\n" +
                        "Actual: [Niff, Naff, Nuff]\n" +
                        "Expected: [Piff, Paff, Puff]\n" +
                        "Collection: .characters\n" +
                        "Timeout: 9 s.\n" +
                        "Caused by: NoSuchElementException: ups"
            )
        )
    }

    @Test
    fun errorMessageWithExplanation() = runBlockingTest {
        val elementWithTextNotFound = ElementWithTextNotFound.ElementWithTextNotFound(
            mockCollection(".characters"),
            actualTexts,
            expectedTexts,
            "we expect favorite characters", 9000, cause
        )
        assertThat(elementWithTextNotFound).hasMessage(
            String.format(
                "Element with text not found\n" +
                        "Actual: [Niff, Naff, Nuff]\n" +
                        "Expected: [Piff, Paff, Puff]\n" +
                        "Because: we expect favorite characters\n" +
                        "Collection: .characters\n" +
                        "Timeout: 9 s.\n" +
                        "Caused by: NoSuchElementException: ups"
            )
        )
    }
}

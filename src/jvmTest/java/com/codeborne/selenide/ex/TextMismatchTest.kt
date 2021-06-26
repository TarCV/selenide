package com.codeborne.selenide.ex

import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.Mocks.mockCollection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class TextMismatchTest : WithAssertions {
    private val collection = mockCollection("Collection description")
    private val actualTexts = Arrays.asList("One", "Two", "Three")
    private val expectedTexts = Arrays.asList("Four", "Five", "Six")
    private val timeoutMs = 1000L
    @BeforeEach
    fun setUp() {
        Mockito.`when`(collection.driver()).thenReturn(DriverStub())
    }

    @Test
    fun toString_withoutExplanation() = runBlockingTest {
        val textsMismatch = TextsMismatch.TextsMismatch(collection, actualTexts, expectedTexts, null, timeoutMs)
        assertThat(textsMismatch).hasMessage(
            String.format(
                "Texts mismatch\n" +
                        "Actual: [One, Two, Three]\n" +
                        "Expected: [Four, Five, Six]\n" +
                        "Collection: Collection description\n" +
                        "Timeout: 1 s."
            )
        )
    }

    @Test
    fun toString_withExplanation() = runBlockingTest {
        val textsMismatch = TextsMismatch.TextsMismatch(collection, actualTexts, expectedTexts, "it's said in doc", timeoutMs)
        assertThat(textsMismatch).hasMessage(
            String.format(
                "Texts mismatch\n" +
                        "Actual: [One, Two, Three]\n" +
                        "Expected: [Four, Five, Six]\n" +
                        "Because: it's said in doc\n" +
                        "Collection: Collection description\n" +
                        "Timeout: 1 s."
            )
        )
    }
}

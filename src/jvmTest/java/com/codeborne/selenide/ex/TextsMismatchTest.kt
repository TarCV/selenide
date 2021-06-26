package com.codeborne.selenide.ex

import com.codeborne.selenide.Mocks.mockCollection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class TextsMismatchTest : WithAssertions {
    private val actualTexts = Arrays.asList("Niff", "Naff", "Nuff")
    private val expectedTexts = Arrays.asList("Piff", "Paff", "Puff")
    @Test
    fun errorMessage() = runBlockingTest {
        val textsMismatch = TextsMismatch.TextsMismatch(
            mockCollection(".characters"),
            actualTexts,
            expectedTexts,
            null, 9000
        )
        assertThat(textsMismatch).hasMessage(
            String.format(
                "Texts mismatch\n" +
                        "Actual: [Niff, Naff, Nuff]\n" +
                        "Expected: [Piff, Paff, Puff]\n" +
                        "Collection: .characters\n" +
                        "Timeout: 9 s."
            )
        )
    }

    @Test
    fun errorMessage_withExplanation() = runBlockingTest {
        val textsMismatch = TextsMismatch.TextsMismatch(
            mockCollection(".characters"),
            actualTexts,
            expectedTexts,
            "we expect favorite characters", 9000
        )
        assertThat(textsMismatch).hasMessage(
            String.format(
                "Texts mismatch\n" +
                        "Actual: [Niff, Naff, Nuff]\n" +
                        "Expected: [Piff, Paff, Puff]\n" +
                        "Because: we expect favorite characters\n" +
                        "Collection: .characters\n" +
                        "Timeout: 9 s."
            )
        )
    }
}

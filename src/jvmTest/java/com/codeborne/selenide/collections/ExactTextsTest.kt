package com.codeborne.selenide.collections

import assertk.all
import assertk.assertions.contains
import assertk.assertions.endsWith
import assertk.assertions.hasMessage
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.Mocks.mockElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.TextsMismatch
import com.codeborne.selenide.ex.TextsSizeMismatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class ExactTextsTest : WithAssertions {
    @Test
    fun varArgsConstructor() {
        val exactTexts = ExactTexts("One", "Two", "Three")
        assertThat(exactTexts.expectedTexts)
            .`as`("Expected texts list")
            .isEqualTo(Arrays.asList("One", "Two", "Three"))
    }

    @Test
    fun applyOnWrongSizeList() {
        val exactTexts = ExactTexts("One", "Two", "Three")
        assertThat(exactTexts.test(listOf(Mockito.mock(WebElement::class.java))))
            .isFalse
    }

    @Test
    fun applyOnCorrectSizeAndCorrectElementsText() {
        val exactTexts = ExactTexts("One", "Two")
        val webElement1: WebElement = mockElement("One")
        val webElement2: WebElement = mockElement("Two")
        assertThat(exactTexts.test(Arrays.asList(webElement1, webElement2)))
            .isTrue
    }

    @Test
    fun applyOnCorrectListSizeButWrongElementsText() {
        val exactTexts = ExactTexts("One", "Two")
        val webElement1: WebElement = mockElement("One")
        val webElement2: WebElement = mockElement("One")
        assertThat(exactTexts.test(Arrays.asList(webElement1, webElement2)))
            .isFalse
    }

    @Test
    fun failWithNullElementsList() {
        failOnEmptyOrNullElementsList(null)
    }

    private fun failOnEmptyOrNullElementsList(elements: List<WebElement>?) = runBlockingTest {
        val exactTexts = ExactTexts("One")
        val cause: RuntimeException = IllegalArgumentException("bad thing happened")
        assertk.assertThat { exactTexts.fail(mockCollection("Collection description"), elements, cause, 10000) }
            .isFailure()
            .isInstanceOf(ElementNotFound::class.java)
            .hasMessage(
                String.format(
                    "Element not found {Collection description}\nExpected: Exact texts [One]\n" +
                            "Timeout: 10 s.\n" +
                            "Caused by: java.lang.IllegalArgumentException: bad thing happened"
                )
            )
    }

    @Test
    fun failWithEmptyElementsLIst() {
        failOnEmptyOrNullElementsList(emptyList())
    }

    @Test
    fun failOnTextMismatch() = runBlockingTest {
        val exactTexts = ExactTexts("One")
        val cause = Exception("Exception method")
        val mockedWebElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`(mockedWebElement.text).thenReturn("Hello")
        assertk.assertThat {
            exactTexts.fail(
                mockCollection("Collection description"),
                listOf(mockedWebElement),
                cause,
                10000
            )
        }
            .isFailure()
            .isInstanceOf(TextsMismatch::class.java)
            .hasMessage(
                String.format(
                    "Texts mismatch\n" +
                            "Actual: [Hello]\n" +
                            "Expected: [One]\n" +
                            "Collection: Collection description\n" +
                            "Timeout: 10 s."
                )
            )
    }

    @Test
    fun failOnTextSizeMismatch() = runBlockingTest {
        val exactTexts = ExactTexts("One", "Two")
        val cause = Exception("Exception method")
        val webElement: WebElement = mockElement("One")
        assertk.assertThat {
            exactTexts.fail(
                mockCollection("Collection description"),
                listOf(webElement), cause, 10000
            )
        }
            .isFailure()
            .isInstanceOf(TextsSizeMismatch::class.java)
            .message()
            .isNotNull()
            .all {
                contains("Actual: [One], List size: 1")
                contains("Expected: [One, Two], List size: 2")
                endsWith(String.format("Collection: Collection description\nTimeout: 10 s."))
            }
    }

    @Test
    fun testToString() {
        val exactTexts = ExactTexts("One", "Two", "Three")
        assertThat<ExactTexts>(exactTexts)
            .hasToString("Exact texts [One, Two, Three]")
    }

    @Test
    fun emptyArrayIsNotAllowed() {
        assertThatThrownBy { ExactTexts() }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("No expected texts given")
    }

    @Test
    fun emptyListIsNotAllowed() {
        assertThatThrownBy { ExactTexts(emptyList()) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("No expected texts given")
    }
}

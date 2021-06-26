package com.codeborne.selenide.collections

import assertk.all
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.startsWith
import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.ex.ListSizeMismatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class SizeGreaterThanOrEqualTest : WithAssertions {
    @Test
    fun applyWithEmptyList() {
        assertThat(SizeGreaterThanOrEqual(10).test(emptyList()))
            .isFalse
    }

    @Test
    fun applyWithWrongSizeList() {
        assertThat(SizeGreaterThanOrEqual(10).test(listOf(Mockito.mock(WebElement::class.java))))
            .isFalse
    }

    @Test
    fun applyWithSameSize() {
        assertThat(SizeGreaterThanOrEqual(1).test(listOf(Mockito.mock(WebElement::class.java))))
            .isTrue
    }

    @Test
    fun applyWithGreaterSize() {
        assertThat(
            SizeGreaterThanOrEqual(1).test(
                Arrays.asList(
                    Mockito.mock(
                        WebElement::class.java
                    ), Mockito.mock(WebElement::class.java)
                )
            )
        )
            .isTrue
    }

    @Test
    fun failMethod() = runBlockingTest {
        val collection = mockCollection("Collection description")
        assertk.assertThat {
            SizeGreaterThanOrEqual(10).fail(
                collection,
                emptyList(),
                Exception("Exception message"),
                10000
            )
        }
            .isFailure()
            .isInstanceOf(ListSizeMismatch::class.java)
            .message()
            .isNotNull()
            .all {
                startsWith(
                    String.format(
                        "List size mismatch: " +
                            "expected: >= 10, actual: 0, collection: Collection description\n" +
                            "Elements: []"
                    )
                )
            }
    }

    @Test
    fun testToString() {
        assertThat<SizeGreaterThanOrEqual>(SizeGreaterThanOrEqual(10))
            .hasToString("size >= 10")
    }
}

package com.codeborne.selenide.collections

import assertk.all
import assertk.assertions.contains
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import com.codeborne.selenide.Mocks.mockElement
import com.codeborne.selenide.Mocks.mockCollection
import org.assertj.core.api.WithAssertions
import com.codeborne.selenide.ex.ElementWithTextNotFound
import com.codeborne.selenide.ElementsCollection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import java.lang.Exception

@ExperimentalCoroutinesApi
internal class ItemWithTextTest : WithAssertions {
    private val element1 = mockElement("Test-One")
    private val element2 = mockElement("Test-Two")
    private val element3 = mockElement("Test-Three")
    private val collection = mockCollection("Collection description", element1, element2, element3)
    @Test
    fun applyOnCorrectElementText() = runBlockingTest {
        assertThat(
            ItemWithText("Test-One")
                .test(collection.getElements())
        )
            .isTrue
    }

    @Test
    fun applyOnWrongElementText() = runBlockingTest {
        assertThat(
            ItemWithText("Test-X")
                .test(collection.getElements())
        )
            .isFalse
    }

    @Test
    fun testToString() {
        assertThat<ItemWithText>(ItemWithText("Test-One"))
            .hasToString("Text Test-One")
    }

    @Test
    fun testApplyWithEmptyList() = runBlockingTest {
        val emptyCollection = mockCollection("empty collection")
        assertThat(
            ItemWithText("Test-X")
                .test(emptyCollection.getElements())
        )
            .isFalse
    }

    @Test
    fun failOnMatcherError() {
        runBlockingTest {
            val expectedText = "Won't exist"
            val texts = ElementsCollection.texts(collection.getElements())

            assertk.assertThat {
                ItemWithText(expectedText)
                    .fail(
                        collection,
                        collection.getElements(),
                        Exception("Exception message"), 10000
                    )
            }
                .isFailure()
                .isInstanceOf(ElementWithTextNotFound::class.java)
                .message()
                .isNotNull()
                .all {
                    contains(
                        String.format(
                            String.format(
                                "Element with text not found" +
                                    "%nActual: %s" +
                                    "%nExpected: %s",
                                texts,
                                listOf(expectedText)
                            )
                        )
                    )
                }
        }
    }
}

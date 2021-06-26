package com.codeborne.selenide.collections

import assertk.all
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.startsWith
import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.Mocks.mockElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.MatcherError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class NoneMatchTest : WithAssertions {
    private val element1 = mockElement("Test-One")
    private val element2 = mockElement("Test-Two")
    private val element3 = mockElement("Test-Three")
    private val collection = mockCollection("Collection description", element1, element2, element3)
    @Test
    fun applyWithEmptyList() = runBlockingTest {
        assertThat(NoneMatch("Predicate description") { it.getText().equals("EmptyList") }
            .test(mockCollection("Collection description").getElements()))
            .isFalse
    }

    @Test
    fun applyWithMatchingPredicate() = runBlockingTest {
        assertThat(NoneMatch("Predicate description") { it.getText().contains("Test") }
            .test(collection.getElements()))
            .isFalse
    }

    @Test
    fun applyWithNonMatchingPredicate() = runBlockingTest {
        assertThat(NoneMatch("Predicate description") { it.getText().equals("NotPresent") }
            .test(collection.getElements()))
            .isTrue
    }

    @Test
    fun failOnEmptyCollection() = runBlockingTest {
        assertk.assertThat {
            NoneMatch("Predicate description") { it.getText().equals("Test") }
                .fail(
                    mockCollection("Collection description"),
                    emptyList(),
                    Exception("Exception message"), 10000
                )
        }
            .isFailure()
            .isInstanceOf(ElementNotFound::class.java)
    }

    @Test
    fun failOnMatcherError() = runBlockingTest {
        assertk.assertThat {
            NoneMatch("Predicate description") { it.getText().contains("One") }
                .fail(
                    collection,
                    collection.getElements(),
                    Exception("Exception message"), 10000
                )
        }
            .isFailure()
            .isInstanceOf(MatcherError::class.java)
            .message()
            .isNotNull()
            .all {
                startsWith(
                    String.format(
                        "Collection matcher error" +
                            "\nExpected: none of elements to match [Predicate description] predicate" +
                            "\nCollection: Collection description"
                    )
                )
            }
    }

    @Test
    fun testToString() {
        assertThat<NoneMatch>(NoneMatch("Predicate description") { true })
            .hasToString("none match [Predicate description] predicate")
    }
}

package com.codeborne.selenide.collections

import assertk.all
import assertk.assertThat
import assertk.assertions.hasToString
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import assertk.assertions.message
import assertk.assertions.startsWith
import com.codeborne.selenide.Mocks.mockElement
import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.ex.MatcherError
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import java.lang.Exception

@ExperimentalCoroutinesApi
internal class AllMatchTest {
    private val element1 = mockElement("Test-One")
    private val element2 = mockElement("Test-Two")
    private val element3 = mockElement("Test-Three")
    private val collection = mockCollection("Collection description", element1, element2, element3)

    @Test
    fun applyWithEmptyList() = runBlockingTest {
        assertThat(AllMatch("Predicate description") { it.text.equals("EmptyList") }
            .test(mockCollection("Collection description").getElements()))
            .isFalse()
    }

    @Test
    fun applyWithNonMatchingPredicate() = runBlockingTest {
        assertThat(AllMatch("Predicate description") { it.text.equals("NotPresent") }
            .test(collection.getElements()))
            .isFalse()
    }

    @Test
    fun applyWithMatchingPredicate() = runBlockingTest {
        assertThat(AllMatch("Predicate description") { it.text.contains("Test") }
            .test(collection.getElements()))
            .isTrue()
    }

    @Test
    fun failOnMatcherError() = runBlockingTest {
        assertThat {
            AllMatch("Predicate description") { it.text.contains("Foo") }
                .fail(
                    collection,
                    collection.getElements(),
                    Exception("Exception message"), 10000
                )
        }
            .isFailure()
            .isInstanceOf(MatcherError::class)
            .message()
            .isNotNull()
            .all {
                startsWith(
                    String.format(
                        "Collection matcher error" +
                            "%nExpected: all of elements to match [Predicate description] predicate" +
                            "%nCollection: Collection description"
                    )
                )
            }
    }

    @Test
    fun failOnEmptyCollection() = runBlockingTest {
        assertThat {
            AllMatch("Predicate description") { it.getText().equals("Test") }
                .fail(
                    mockCollection("Collection description"),
                    listOf(),
                    Exception("Exception message"), 10000
                )
        }
            .isFailure()
            .isInstanceOf(ElementNotFound::class.java)
    }

    @Test
    fun testToString() {
        assertThat<AllMatch>(AllMatch("Predicate description") { true })
            .hasToString("all match [Predicate description] predicate")
    }
}

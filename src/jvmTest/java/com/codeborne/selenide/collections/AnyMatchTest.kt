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
internal class AnyMatchTest {
    private val element1 = mockElement("Hello")
    private val element2 = mockElement("World")
    @Test
    fun applyWithEmptyList()  = runBlockingTest {
        assertThat(AnyMatch("Predicate description") { it.getText().equals("World") }
            .test(mockCollection("Collection description").getElements()))
            .isFalse()
    }

    @Test
    fun applyWithNonMatchingPredicate()  = runBlockingTest {
        assertThat(AnyMatch("Predicate description") { it.getText().equals("World") }
            .test(listOf(element1)))
            .isFalse()
    }

    @Test
    fun applyWithMatchingPredicate()  = runBlockingTest {
        val collection = mockCollection("Collection description", element1, element2)
        assertThat(AnyMatch("Predicate description") { it.getText().equals("World") }
            .test(collection.getElements()))
            .isTrue()
    }

    @Test
    fun failOnMatcherError()  = runBlockingTest {
        val collection = mockCollection("Collection description")
        assertThat {
            AnyMatch("Predicate description") { it.getText().equals("World") }
                .fail(
                    collection,
                    listOf(element1),
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
                                "%nExpected: any of elements to match [Predicate description] predicate" +
                                "%nCollection: Collection description"
                        )
                    )
            }
    }

    @Test
    fun failOnEmptyCollection()  = runBlockingTest {
        assertThat {
            AnyMatch("Predicate description") { it.getText().equals("World") }
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
    fun testToString()  = runBlockingTest {
        assertThat<AnyMatch>(AnyMatch("Predicate description") { true })
            .hasToString("any match [Predicate description] predicate")
    }
}

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
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.Mocks.mockElement
import com.codeborne.selenide.ex.DoesNotContainTextsError
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import java.util.Arrays

@ExperimentalCoroutinesApi
class ContainExactTextsCaseSensitiveTest {
    private val element1 = mockElement("Test-One")
    private val element2 = mockElement("Test-Two")
    private val element3 = mockElement("Test-Three")
    private val element4 = mockElement("Test-Four")
    private val element5 = mockElement("Test-Five")
    private val collection = mockCollection(
        "Collection with all 3 expected elements",
        element1, element2, element3
    )
    private val collectionMoreElements = mockCollection(
        "Collection with more elements that includes all the expected elements",
        element1, element2, element3, element4
    )
    private val collectionMoreElementsAndDuplicates = mockCollection(
        "Collection with more elements than expected " +
                "that contains duplicates of expected elements and includes all the expected elements",
        element1, element2, element3, element4, element2, element3
    )
    private val collectionWithoutElement = mockCollection(
        "Collection without one of the expected texts",
        element1, element2, element4
    )
    private val collectionMoreElementsWithoutTwoElements = mockCollection(
        "Collection with more elements than expected and without two of the expected texts",
        element1, element4, element5, element1, element4
    )
    private val collectionLessElementsWithoutElement = mockCollection(
        "Collection with less elements than expected and without one of the expected texts",
        element1, element2
    )
    private val emptyCollection = mockCollection("Empty collection")
    @Test
    fun testExactCollection() = runBlockingTest {
        val expectedTexts = ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
        assertThat(expectedTexts.test(collection.getElements()))
            .isTrue()
    }

    @Test
    fun testCollectionUnordered() = runBlockingTest {
        val expectedTexts = ContainExactTextsCaseSensitive("Test-Three", "Test-One", "Test-Two")
        assertThat(expectedTexts.test(collection.getElements()))
            .isTrue()
    }

    @Test
    fun testCollectionUnorderedMoreElements() = runBlockingTest {
        val expectedTexts = ContainExactTextsCaseSensitive("Test-Three", "Test-One", "Test-Two")
        assertThat(expectedTexts.test(collectionMoreElements.getElements()))
            .isTrue()
    }

    @Test
    fun testCollectionUnorderedMoreElementsWithDuplicates() = runBlockingTest {
        val expectedTexts = ContainExactTextsCaseSensitive("Test-Three", "Test-One", "Test-Two")
        assertThat(expectedTexts.test(collectionMoreElementsAndDuplicates.getElements()))
            .isTrue()
    }

    @Test
    fun testCollectionWithoutElement() {
        runBlockingTest {
            val expectedTexts = ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
            assertThat(expectedTexts.test(collectionWithoutElement.getElements()))
                .isFalse()

            val elements = ElementsCollection.texts(collectionWithoutElement.getElements())
            assertThat {
                ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
                    .fail(
                        collectionWithoutElement,
                        collectionWithoutElement.getElements(),
                        Exception("Exception message"), 10000
                    )
            }
                .isFailure()
                .isInstanceOf(DoesNotContainTextsError::class.java)
                .message()
                .isNotNull()
                .all {
                    startsWith(
                        String.format(
                            "The collection with text elements: %s\n" +
                                "should contain all of the following text elements: %s\n" +
                                "but could not find these elements: %s",
                            elements,
                            Arrays.asList("Test-One", "Test-Two", "Test-Three"), listOf("Test-Three")
                        )
                    )
                }
        }
    }

    @Test
    fun testCollectionMoreElementsWithoutSomeElements() {
        runBlockingTest {
            val expectedTexts = ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
            assertThat(expectedTexts.test(collectionMoreElementsWithoutTwoElements.getElements()))
                .isFalse()

            val elements = collectionMoreElementsWithoutTwoElements.getElements()
            assertThat {
                ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
                    .fail(
                        collectionMoreElementsWithoutTwoElements,
                        collectionMoreElementsWithoutTwoElements.getElements(),
                        Exception("Exception message"), 10000
                    )
            }
                .isFailure()
                .isInstanceOf(DoesNotContainTextsError::class.java)
                .message()
                .isNotNull()
                .all {
                    startsWith(
                        String.format(
                            "The collection with text elements: %s\n" +
                                "should contain all of the following text elements: %s\n" +
                                "but could not find these elements: %s",
                            ElementsCollection.texts(elements),
                            Arrays.asList("Test-One", "Test-Two", "Test-Three"),
                            Arrays.asList("Test-Two", "Test-Three")
                        )
                    )
                }
        }
    }

    @Test
    fun testCollectionLessElementsWithoutElement() {
        runBlockingTest {
            val expectedTexts = ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
            assertThat(expectedTexts.test(collectionLessElementsWithoutElement.getElements()))
                .isFalse()

            val elements = collectionLessElementsWithoutElement.getElements()
            assertThat {
                ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
                    .fail(
                        collectionLessElementsWithoutElement,
                        collectionLessElementsWithoutElement.getElements(),
                        Exception("Exception message"), 10000
                    )
            }
                .isFailure()
                .isInstanceOf(DoesNotContainTextsError::class.java)
                .message()
                .isNotNull()
                .all {
                    startsWith(
                        String.format(
                            "The collection with text elements: %s\n" +
                                "should contain all of the following text elements: %s\n" +
                                "but could not find these elements: %s",
                            ElementsCollection.texts(elements),
                            Arrays.asList("Test-One", "Test-Two", "Test-Three"), listOf("Test-Three")
                        )
                    )
                }
        }
    }

    @Test
    fun testCollectionNullElements() = runBlockingTest {
        val expectedTexts = ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
        assertThat {
            expectedTexts
                .fail(
                    emptyCollection,
                    null,
                    Exception("Exception message"), 10000
                )
        }
            .isFailure()
            .isInstanceOf(ElementNotFound::class.java)
            .message()
            .isNotNull()
            .all {
                startsWith(
                    String.format(
                        "Element not found {Empty collection}" +
                            "\nExpected: Contains exact texts case-sensitive [Test-One, Test-Two, Test-Three]"
                    )
                )
            }
    }

    @Test
    fun testCollectionEmpty() = runBlockingTest {
        val expectedTexts = ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
        assertThat {
            expectedTexts
                .fail(
                    emptyCollection,
                    emptyCollection.getElements(),
                    Exception("Exception message"), 10000
                )
        }
            .isFailure()
            .isInstanceOf(ElementNotFound::class.java)
            .message()
            .isNotNull()
            .all {
                startsWith(
                    String.format(
                        "Element not found {Empty collection}" +
                            "\nExpected: Contains exact texts case-sensitive [Test-One, Test-Two, Test-Three]"
                    )
                )
            }
    }

    @Test
    fun testToString() = runBlockingTest {
        val expectedTexts = ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
        assertThat<ContainExactTextsCaseSensitive>(expectedTexts)
            .hasToString("Contains exact texts case-sensitive [Test-One, Test-Two, Test-Three]")
    }

    @Test
    fun testApplyNull() = runBlockingTest {
        val expectedTexts = ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
        assertThat(expectedTexts.applyNull())
            .isFalse()
    }
}

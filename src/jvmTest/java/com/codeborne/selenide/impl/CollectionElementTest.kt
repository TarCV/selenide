package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class CollectionElementTest : WithAssertions {
    private val driver: Driver = DriverStub()
    @Test
    fun wrap() {
        val mockedWebElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`(mockedWebElement.tagName).thenReturn("a")
        Mockito.`when`(mockedWebElement.isDisplayed).thenReturn(true)
        Mockito.`when`(mockedWebElement.text).thenReturn("selenide")
        val collection: CollectionSource = WebElementsCollectionWrapper(driver, listOf(mockedWebElement))
        val selenideElement = CollectionElement.wrap(collection, 0)
        assertThat(selenideElement)
            .hasToString("<a>selenide</a>")
    }

    @Test
    fun getWebElement() = runBlockingTest {
        val mockedWebElement1 = Mockito.mock(WebElement::class.java)
        val mockedWebElement2 = Mockito.mock(WebElement::class.java)
        val collection = mockCollection("", mockedWebElement1, mockedWebElement2)
        val collectionElement = CollectionElement(collection, 1)
        assertThat<Any>(collectionElement.getWebElement()).isEqualTo(mockedWebElement2)
    }

    @Test
    fun getSearchCriteria() = runBlockingTest {
        val collectionDescription = "Collection description"
        val index = 1
        val collection = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`<Any>(collection.description()).thenReturn(collectionDescription)
        val collectionElement = CollectionElement(collection, 1)
        assertThat<Any>(collectionElement.getSearchCriteria())
            .isEqualTo(String.format("%s[%s]", collectionDescription, index))
    }

    @Test
    fun testToString() = runBlockingTest {
        val collection = Mockito.mock(CollectionSource::class.java)
        val collectionDescription = "Collection description"
        Mockito.`when`<Any>(collection.description()).thenReturn(collectionDescription)
        val index = 1
        val collectionElement = CollectionElement(collection, 1)
        assertThat(collectionElement)
            .hasToString(String.format("%s[%s]", collectionDescription, index))
    }

    @Test
    fun createElementNotFoundErrorWithEmptyCollection() = runBlockingTest {
        val collection = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`(collection.driver()).thenReturn(driver)
        Mockito.`when`<Any>(collection.description()).thenReturn("Collection description")
        val collectionElement = CollectionElement(collection, 33)
        val mockedCollection = Mockito.mock(Condition::class.java)
        val elementNotFoundError =
            collectionElement.createElementNotFoundError(mockedCollection, Error("Error message"))
        assertThat(elementNotFoundError)
            .hasMessage(
                String.format(
                    "Element not found {Collection description[33]}\n" +
                            "Expected: visible\n" +
                            "Timeout: 0 ms.\n" +
                            "Caused by: java.lang.Error: Error message"
                )
            )
    }

    @Test
    fun createElementNotFoundErrorWithNonEmptyCollection() = runBlockingTest {
        val collection = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`(collection.driver()).thenReturn(driver)
        Mockito.`when`<Any>(collection.description()).thenReturn("Collection description")
        Mockito.`when`<Any>(collection.getElements()).thenReturn(
            listOf(
                Mockito.mock(
                    WebElement::class.java
                )
            )
        )
        val collectionElement = CollectionElement(collection, 1)
        val mockedCollection = Mockito.mock(Condition::class.java)
        Mockito.`when`(mockedCollection.toString()).thenReturn("Reason description")
        val elementNotFoundError =
            collectionElement.createElementNotFoundError(mockedCollection, Error("Error message"))
        assertThat(elementNotFoundError)
            .hasMessage(
                String.format(
                    "Element not found {Collection description[1]}\n" +
                            "Expected: Reason description\n" +
                            "Timeout: 0 ms.\n" +
                            "Caused by: java.lang.Error: Error message"
                )
            )
    }
}

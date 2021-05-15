package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.impl.CollectionElementByCondition.Companion.wrap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class CollectionElementByConditionTest : WithAssertions {
    private val driver: Driver = DriverStub()
    @Test
    fun wrap() {
        val mockedWebElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`(mockedWebElement.tagName).thenReturn("a")
        Mockito.`when`(mockedWebElement.isDisplayed).thenReturn(true)
        Mockito.`when`(mockedWebElement.text).thenReturn("selenide")
        val selenideElement = wrap(
            WebElementsCollectionWrapper(driver, listOf(mockedWebElement)), Condition.visible
        )
        assertThat(selenideElement)
            .hasToString("<a>selenide</a>")
    }

    @Test
    fun getWebElement() = runBlockingTest {
        val collection = Mockito.mock(CollectionSource::class.java)
        val mockedWebElement1 = Mockito.mock(WebElement::class.java)
        val mockedWebElement2 = Mockito.mock(WebElement::class.java)
        val driver = DriverStub()
        val listOfMockedElements = Arrays.asList(mockedWebElement1, mockedWebElement2)
        Mockito.`when`(collection.driver()).thenReturn(driver)
        Mockito.`when`<Any>(collection.getElements()).thenReturn(listOfMockedElements)
        Mockito.`when`(mockedWebElement2.isDisplayed).thenReturn(true)
        val collectionElement = CollectionElementByCondition(collection, Condition.visible)
        assertThat<Any>(collectionElement.getWebElement())
            .isEqualTo(mockedWebElement2)
    }

    @Test
    fun getSearchCriteria() = runBlockingTest {
        val collection = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`<Any>(collection.description()).thenReturn("ul#employees li.employee")
        val collectionElement = CollectionElementByCondition(collection, Condition.visible)
        assertThat(collectionElement)
            .hasToString(String.format("%s.findBy(visible)", "ul#employees li.employee"))
    }

    @Test
    fun testToString() = runBlockingTest {
        val collection = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`<Any>(collection.description()).thenReturn("ul#employees li.employee")
        val collectionElement = CollectionElementByCondition(collection, Condition.visible)
        assertThat(collectionElement)
            .hasToString(String.format("%s.findBy(visible)", "ul#employees li.employee"))
    }

    @Test
    fun createElementNotFoundErrorWithEmptyCollection() = runBlockingTest {
        val collection = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`(collection.driver()).thenReturn(driver)
        Mockito.`when`<Any>(collection.description()).thenReturn("ul#employees li.employee")
        val collectionElement = CollectionElementByCondition(collection, Condition.visible)
        val elementNotFoundError = collectionElement.createElementNotFoundError(
            Condition.visible,
            NoSuchElementException("with class: employee")
        )
        assertThat(elementNotFoundError)
            .hasMessage(
                String.format(
                    "Element not found {ul#employees li.employee.findBy(visible)}%n" +
                            "Expected: visible%n" +
                            "Timeout: 0 ms.%n" +
                            "Caused by: NoSuchElementException: with class: employee"
                )
            )
    }

    @Test
    fun createElementNotFoundErrorWithNonEmptyCollection() = runBlockingTest {
        val collection = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`(collection.driver()).thenReturn(driver)
        Mockito.`when`<Any>(collection.description()).thenReturn("ul#employees li.employee")
        Mockito.`when`<Any>(collection.getElements()).thenReturn(
            listOf(
                Mockito.mock(
                    WebElement::class.java
                )
            )
        )
        val collectionElement = CollectionElementByCondition(collection, Condition.visible)
        val mockedCollection = Mockito.mock(Condition::class.java)
        Mockito.`when`(mockedCollection.toString()).thenReturn("Reason description")
        val elementNotFoundError =
            collectionElement.createElementNotFoundError(mockedCollection, Error("Error message"))
        assertThat(elementNotFoundError)
            .hasMessage(
                String.format(
                    "Element not found {ul#employees li.employee.findBy(visible)}%n" +
                            "Expected: Reason description%n" +
                            "Timeout: 0 ms.%n" +
                            "Caused by: java.lang.Error: Error message"
                )
            )
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class CollectionSnapshotTest {
    private val originalCollection = Mockito.mock(CollectionSource::class.java)
    @AfterEach
    fun verifyNoMoreInteractionsOnOriginalCollection() = runBlockingTest {
        Mockito.verify(originalCollection).getElements()
        Mockito.verifyNoMoreInteractions(originalCollection)
    }

    // Returns snapshot version nevertheless how many times we call getElements()
    @Test
    fun getOriginalCollectionSnapshotElements() = runBlockingTest {
        val mockedWebElement1 = Mockito.mock(WebElement::class.java)
        val mockedWebElement2 = Mockito.mock(WebElement::class.java)
        val originalCollectionElements = Arrays.asList(mockedWebElement1, mockedWebElement2)
        Mockito.`when`<Any>(originalCollection.getElements()).thenReturn(originalCollectionElements)
        val collectionSnapshot = CollectionSnapshot(originalCollection)
        val snapshotCollectionElements = collectionSnapshot.getElements()
        Assertions.assertThat(snapshotCollectionElements).isNotSameAs(originalCollectionElements)
        Assertions.assertThat(snapshotCollectionElements).isEqualTo(originalCollectionElements)

        // Returns snapshot version nevertheless how many times we call getElements()
        val snapshotCollectionElements2 = collectionSnapshot.getElements()
        Assertions.assertThat(snapshotCollectionElements2).isSameAs(snapshotCollectionElements)
        Assertions.assertThat(snapshotCollectionElements2).isEqualTo(snapshotCollectionElements)
    }

    @Test
    fun getOriginalCollectionSnapshotElement() = runBlockingTest {
        val mockedWebElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`<Any>(originalCollection.getElements()).thenReturn(listOf(mockedWebElement))
        val collectionElement = CollectionSnapshot(originalCollection).getElement(0)
        Assertions.assertThat(collectionElement).isEqualTo(mockedWebElement)
    }

    @Test
    fun description() = runBlockingTest {
        val mockedWebElement1 = Mockito.mock(WebElement::class.java)
        val mockedWebElement2 = Mockito.mock(WebElement::class.java)
        Mockito.`when`<Any>(originalCollection.description()).thenReturn("Collection description")
        Mockito.`when`<Any>(originalCollection.getElements())
            .thenReturn(Arrays.asList(mockedWebElement1, mockedWebElement2))
        val collectionSnapshot = CollectionSnapshot(originalCollection)
        Assertions.assertThat<Any>(collectionSnapshot.description())
            .isEqualTo("Collection description.snapshot(2 elements)")
        // Call one more time to check that getElements() is executed only once
        Assertions.assertThat<Any>(collectionSnapshot.description())
            .isEqualTo("Collection description.snapshot(2 elements)")
        Mockito.verify(originalCollection, Mockito.times(2)).description()
    }

    @Test
    fun driver() {
        val driver = Mockito.mock(Driver::class.java)
        Mockito.`when`(originalCollection.driver()).thenReturn(driver)
        val collectionSnapshot = CollectionSnapshot(originalCollection)
        Assertions.assertThat(collectionSnapshot.driver()).isEqualTo(driver)
        Mockito.verify(originalCollection).driver()
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class CollectionSnapshotTest {
    private val originalCollection: CollectionSource = mock()

    suspend fun verifyNoMoreInteractionsOnOriginalCollection() {
        verify(originalCollection).getElements()
        verifyNoMoreInteractions(originalCollection)
    }

    // Returns snapshot version nevertheless how many times we call getElements()
    @Test
    fun getOriginalCollectionSnapshotElements() = runBlockingTest {
        val mockedWebElement1: WebElement = mock()
        val mockedWebElement2: WebElement = mock()
        val originalCollectionElements = listOf(mockedWebElement1, mockedWebElement2)
        stub {
            on(originalCollection.getElements()).thenReturn(originalCollectionElements)
        }
        val collectionSnapshot = CollectionSnapshot(originalCollection)
        val snapshotCollectionElements = collectionSnapshot.getElements()
        Assertions.assertThat(snapshotCollectionElements).isNotSameAs(originalCollectionElements)
        Assertions.assertThat(snapshotCollectionElements).isEqualTo(originalCollectionElements)

        // Returns snapshot version nevertheless how many times we call getElements()
        val snapshotCollectionElements2 = collectionSnapshot.getElements()
        Assertions.assertThat(snapshotCollectionElements2).isSameAs(snapshotCollectionElements)
        Assertions.assertThat(snapshotCollectionElements2).isEqualTo(snapshotCollectionElements)

        verifyNoMoreInteractionsOnOriginalCollection()
    }

    @Test
    fun getOriginalCollectionSnapshotElement() = runBlockingTest {
        val mockedWebElement: WebElement = mock()
        stub {
            on(originalCollection.getElements()).thenReturn(listOf(mockedWebElement))
        }
        val collectionElement = CollectionSnapshot(originalCollection).getElement(0)
        Assertions.assertThat(collectionElement).isEqualTo(mockedWebElement)

        verifyNoMoreInteractionsOnOriginalCollection()
    }

    @Test
    fun description() = runBlockingTest {
        val mockedWebElement1: WebElement = mock()
        val mockedWebElement2: WebElement = mock()
        stub {
            on(originalCollection.description()).thenReturn("Collection description")
            on(originalCollection.getElements())
                .thenReturn(listOf(mockedWebElement1, mockedWebElement2))
        }
        val collectionSnapshot = CollectionSnapshot(originalCollection)
        Assertions.assertThat<Any>(collectionSnapshot.description())
            .isEqualTo("Collection description.snapshot()")
        // Call one more time to check that getElements() is executed only once
        Assertions.assertThat<Any>(collectionSnapshot.description())
            .isEqualTo("Collection description.snapshot()")
        verify(originalCollection, times(2)).description()
        verifyNoMoreInteractions(originalCollection)
    }

    @Test
    fun driver() {
        val driver: Driver = mock()
        stub {
            on(originalCollection.driver()).thenReturn(driver)
        }
        val collectionSnapshot = CollectionSnapshot(originalCollection)
        Assertions.assertThat(collectionSnapshot.driver()).isEqualTo(driver)
        verify(originalCollection).driver()
        verifyNoMoreInteractions(originalCollection)
    }
}

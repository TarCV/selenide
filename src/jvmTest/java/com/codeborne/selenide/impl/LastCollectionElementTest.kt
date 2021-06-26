package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.be
import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.Mocks.mockElement
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.StaleElementReferenceException

@ExperimentalCoroutinesApi
internal class LastCollectionElementTest : WithAssertions {
    private val element1 = mockElement("Hello")
    private val element2 = mockElement("World")
    private val collection = mockCollection("ul#employees li.employee", element1, element2)
    private val lastCollectionElement = LastCollectionElement(collection)

    @Test
    fun getElementMethod() = runBlockingTest {
        assertThat<Any>(lastCollectionElement.getWebElement()).isEqualTo(element2)
    }

    @Test
    fun getElementMethodWhenStaleElementReferenceExceptionThrown() = runBlockingTest {
        checkGetElementsMethodWithException(StaleElementReferenceException("Something went wrong"))
    }

    private suspend fun <T : Throwable?> checkGetElementsMethodWithException(exception: T) {
        Mockito.doThrow(exception).`when`(element2).isEnabled
        Mockito.`when`<Any>(collection.getElements()).thenReturn(listOf(element1))
        assertThat<Any>(lastCollectionElement.getWebElement()).isEqualTo(element1)
    }

    @Test
    fun getElementMethodWhenIndexOutBoundExceptionThrown() = runBlockingTest {
        checkGetElementsMethodWithException(IndexOutOfBoundsException())
    }

    @Test
    fun createElementNotFoundErrorMethodWhenCollectionIsEmpty() = runBlockingTest {
        Mockito.`when`<Any>(collection.getElements()).thenReturn(emptyList<Any>())
        val notFoundError = lastCollectionElement
            .createElementNotFoundError(be(Condition.empty), StaleElementReferenceException("stale error"))
        assertThat(notFoundError)
            .hasMessageStartingWith(String.format("Element not found {ul#employees li.employee:last}\nExpected: visible"))
    }

    @Test
    fun createElementNotFoundErrorMethodWhenCollectionIsNotEmpty() = runBlockingTest {
        val notFoundError = lastCollectionElement
            .createElementNotFoundError(be(Condition.empty), StaleElementReferenceException("stale error"))
        assertThat(notFoundError)
            .hasMessageStartingWith(String.format("Element not found {ul#employees li.employee:last}\nExpected: be empty"))
    }

    @Test
    fun testToString() {
        assertThat(lastCollectionElement)
            .hasToString("ul#employees li.employee:last")
    }
}

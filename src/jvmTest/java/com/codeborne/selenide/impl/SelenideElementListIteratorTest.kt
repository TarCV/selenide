package com.codeborne.selenide.impl

import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.Mocks.mockCollection
import com.codeborne.selenide.Mocks.mockWebElement
import com.codeborne.selenide.SelenideElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class SelenideElementListIteratorTest : WithAssertions {
    private val webElement = mockWebElement("a", "click me if you can")
    private val collection = mockCollection("Collection description", webElement)
    @Test
    fun hasPrevious() = runBlockingTest {
        val selenideElementIterator = ElementsCollection(collection).asReversedFlow(1)
        assertThat(selenideElementIterator.take(1).singleOrNull()).isNotNull
    }

    @Test
    fun previous() = runBlockingTest {
        val mockedWebElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`(mockedWebElement.isDisplayed).thenReturn(true)
        Mockito.`when`(mockedWebElement.tagName).thenReturn("a")
        Mockito.`when`(mockedWebElement.text).thenReturn("selenide")
        Mockito.`when`<Any>(collection.getElements()).thenReturn(listOf(mockedWebElement))
        val selenideElementIterator = ElementsCollection(collection).asReversedFlow(1)
        val previous: SelenideElement? = selenideElementIterator.take(1).singleOrNull()
        assertThat(previous).isNotNull
        assertThat(previous!!.describe()).isEqualTo("<a>click me if you can</a>")
    }
}

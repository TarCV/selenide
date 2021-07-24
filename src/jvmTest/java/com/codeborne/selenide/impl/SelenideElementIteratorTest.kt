package com.codeborne.selenide.impl

import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.Mocks.mockWebElement
import com.codeborne.selenide.Mocks.mockCollection
import org.assertj.core.api.WithAssertions
import com.codeborne.selenide.impl.CollectionSource
import com.codeborne.selenide.SelenideElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import java.lang.UnsupportedOperationException

@ExperimentalCoroutinesApi
internal class SelenideElementIteratorTest : WithAssertions {
    private val webElement = mockWebElement("a", "click me if you can")
    @Test
    fun hasNext() = runBlockingTest {
        val collection = mockCollection("collection with 1 element", webElement)
        val selenideElementIterator = ElementsCollection(collection).asFlow()
        assertThat(selenideElementIterator.singleOrNull()).isNotNull
    }

    @Test
    fun doesNotHasNext() = runBlockingTest {
        val collection = mockCollection("empty collection")
        val selenideElementIterator = ElementsCollection(collection).asFlow()
        assertThat(selenideElementIterator.firstOrNull()).isNull()
    }

    @Test
    fun next() = runBlockingTest {
        val collection = mockCollection("collection with 1 element", webElement)
        val selenideElementIterator = ElementsCollection(collection).asFlow()
        selenideElementIterator.collectIndexed { index, nextElement ->
            assertThat(index).isEqualTo(0)
            assertThat(nextElement).isNotNull
            assertThat(nextElement.describe()).isEqualTo("<a>click me if you can</a>")
        }
    }
}

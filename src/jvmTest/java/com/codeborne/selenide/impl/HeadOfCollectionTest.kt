package com.codeborne.selenide.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class HeadOfCollectionTest : WithAssertions {
    private val element1 = Mockito.mock(WebElement::class.java)
    private val element2 = Mockito.mock(WebElement::class.java)
    private val element3 = Mockito.mock(WebElement::class.java)
    private val originalCollection = Mockito.mock(CollectionSource::class.java)
    @BeforeEach
    fun setUp() = runBlockingTest {
        Mockito.`when`<Any>(originalCollection.getElements()).thenReturn(Arrays.asList(element1, element2, element3))
    }

    @Test
    fun lessThanOriginalSize() = runBlockingTest {
        val `$$` = HeadOfCollection(originalCollection, 2)
        assertThat<Any>(`$$`.getElements())
            .isEqualTo(Arrays.asList(element1, element2))
    }

    @Test
    fun equalToOriginalSize() = runBlockingTest {
        val `$$` = HeadOfCollection(originalCollection, 3)
        assertThat<Any>(`$$`.getElements())
            .isEqualTo(Arrays.asList(element1, element2, element3))
    }

    @Test
    fun greaterThanOriginalSize() = runBlockingTest {
        val `$$` = HeadOfCollection(originalCollection, 4)
        assertThat<Any>(`$$`.getElements())
            .isEqualTo(Arrays.asList(element1, element2, element3))
    }
}

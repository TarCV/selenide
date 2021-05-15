package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.DriverStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class FilteringCollectionTest : WithAssertions {
    @Test
    fun getActualElement() = runBlockingTest {
        val mockedWebElement1 = Mockito.mock(WebElement::class.java)
        val mockedWebElement2 = Mockito.mock(WebElement::class.java)
        val driver = DriverStub()
        Mockito.`when`(mockedWebElement1.isDisplayed).thenReturn(false)
        Mockito.`when`(mockedWebElement2.isDisplayed).thenReturn(true)
        val mockedCollection = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`<Any>(mockedCollection.getElements())
            .thenReturn(Arrays.asList(mockedWebElement1, mockedWebElement2))
        Mockito.`when`(mockedCollection.driver()).thenReturn(driver)
        val filteringCollection = FilteringCollection(mockedCollection, Condition.visible)
        val actualElements = filteringCollection.getElements()
        assertThat(actualElements)
            .hasSize(1)
        assertThat(actualElements[0])
            .isEqualTo(mockedWebElement2)
    }

    @Test
    fun description() = runBlockingTest {
        val mockedCollection = Mockito.mock(CollectionSource::class.java)
        Mockito.`when`<Any>(mockedCollection.description()).thenReturn("Collection description")
        val filteringCollection = FilteringCollection(mockedCollection, Condition.visible)
        assertThat<Any>(filteringCollection.description())
            .isEqualTo("Collection description.filter(visible)")
    }
}

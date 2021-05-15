package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class BySelectorCollectionTest : WithAssertions {
    private val driver = Mockito.mock(Driver::class.java)
    private val mockedWebElement = Mockito.mock(SelenideElement::class.java)
    @Test
    fun testNoParentConstructor() = runBlockingTest {
        val bySelectorCollection = BySelectorCollection(driver, By.id("selenide"))
        val description = bySelectorCollection.description()
        assertThat(description)
            .isEqualTo("By.id: selenide")
    }

    @Test
    fun testWithWebElementParentConstructor() = runBlockingTest {
        Mockito.`when`(mockedWebElement.searchCriteria).thenReturn("By.tagName: a")
        val bySelectorCollection = BySelectorCollection(driver, mockedWebElement, By.name("selenide"))
        val description = bySelectorCollection.description()
        assertThat(description)
            .isEqualTo("By.tagName: a/By.name: selenide")
    }

    @Test
    fun testWithNotWebElementParentConstructor() = runBlockingTest {
        val bySelectorCollection = BySelectorCollection(driver, NotWebElement(), By.name("selenide"))
        val description = bySelectorCollection.description()
        assertThat(description)
            .isEqualTo("By.name: selenide")
    }

    private inner class NotWebElement : SearchContext {
        override fun findElements(by: By): List<WebElement> {
            return listOf(mockedWebElement)
        }

        override fun findElement(by: By): WebElement {
            return mockedWebElement
        }
    }
}

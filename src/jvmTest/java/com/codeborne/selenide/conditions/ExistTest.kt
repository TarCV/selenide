package com.codeborne.selenide.conditions

import com.codeborne.selenide.DriverStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class ExistTest {
    private val condition = Exist()
    private val element = Mockito.mock(WebElement::class.java)
    @Test
    fun negate() {
        Assertions.assertThat(condition.missingElementSatisfiesCondition()).isFalse
        Assertions.assertThat(condition.negate().missingElementSatisfiesCondition()).isTrue
    }

    @Test
    fun name() {
        Assertions.assertThat(condition.name).isEqualTo("exist")
        Assertions.assertThat(condition.negate().name).isEqualTo("not exist")
    }

    @Test
    fun satisfied_if_element_is_visible() = runBlockingTest {
        Mockito.`when`(element.isDisplayed).thenReturn(true)
        Assertions.assertThat(condition.apply(DriverStub(), element)).isTrue()
    }

    @Test
    fun satisfied_if_element_exists_even_if_invisible() = runBlockingTest {
        Mockito.`when`(element.isDisplayed).thenReturn(false)
        Assertions.assertThat(condition.apply(DriverStub(), element)).isTrue()
    }

    @Test
    fun not_satisfied_if_element_is_stolen() = runBlockingTest {
        Mockito.`when`(element.isDisplayed).thenThrow(StaleElementReferenceException::class.java)
        Assertions.assertThat(condition.apply(DriverStub(), element)).isFalse()
    }
}

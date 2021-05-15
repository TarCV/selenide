package com.codeborne.selenide.conditions

import com.codeborne.selenide.DriverStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class VisibleTest {
    private val condition = Visible()
    private val element = Mockito.mock(WebElement::class.java)
    @Test
    fun negate() {
        Assertions.assertThat(condition.missingElementSatisfiesCondition()).isFalse
        Assertions.assertThat(condition.negate().missingElementSatisfiesCondition()).isTrue
    }

    @Test
    fun name() {
        Assertions.assertThat(condition.name).isEqualTo("visible")
        Assertions.assertThat(condition.negate().name).isEqualTo("not visible")
    }

    @Test
    fun satisfied_if_element_is_visible() = runBlockingTest {
        Mockito.`when`(element.isDisplayed).thenReturn(true)
        Assertions.assertThat(condition.apply(DriverStub(), element)).isTrue()
    }

    @Test
    fun not_satisfied_if_element_is_invisible() = runBlockingTest {
        Mockito.`when`(element.isDisplayed).thenReturn(false)
        Assertions.assertThat(condition.apply(DriverStub(), element)).isFalse()
    }

    @Test
    fun actualValue_invisible() = runBlockingTest {
        Assertions.assertThat<Any>(condition.actualValue(DriverStub(), element)).isEqualTo("visible:false")
    }

    @Test
    fun actualValue_visible() = runBlockingTest {
        Mockito.`when`(element.isDisplayed).thenReturn(true)
        Assertions.assertThat<Any>(condition.actualValue(DriverStub(), element)).isEqualTo("visible:true")
    }
}

package com.codeborne.selenide.conditions

import com.codeborne.selenide.DriverStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class HiddenTest {
    private val condition = Hidden()
    private val element = Mockito.mock(WebElement::class.java)
    @Test
    fun negate() {
        Assertions.assertThat(condition.missingElementSatisfiesCondition()).isTrue
        Assertions.assertThat(condition.negate().missingElementSatisfiesCondition()).isFalse
    }

    @Test
    fun name() {
        Assertions.assertThat(condition.name).isEqualTo("hidden")
        Assertions.assertThat(condition.negate().name).isEqualTo("not hidden")
    }

    @Test
    fun satisfied_if_element_is_not_visible() = runBlockingTest {
        Mockito.`when`(element.isDisplayed).thenReturn(false)
        Assertions.assertThat(condition.apply(DriverStub(), element)).isTrue()
    }

    @Test
    fun not_satisfied_if_element_is_visible() = runBlockingTest {
        Mockito.`when`(element.isDisplayed).thenReturn(true)
        Assertions.assertThat(condition.apply(DriverStub(), element)).isFalse()
    }
}

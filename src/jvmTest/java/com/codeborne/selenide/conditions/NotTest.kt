package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class NotTest {
    var originalCondition = Mockito.mock(Condition::class.java)
    var notCondition: Not? = null
    @BeforeEach
    fun commonMockCalls() {
        // constructor call
        Mockito.`when`(originalCondition.name).thenReturn("original condition name")
        notCondition = Not(originalCondition, false)
    }

    @AfterEach
    fun verifyNoMoreInteractions() {
        // constructor call
        Mockito.verify(originalCondition).name
        Mockito.verifyNoMoreInteractions(originalCondition)
    }

    @get:Test
    val name: Unit
        get() {
            Assertions.assertThat(notCondition!!.name).isEqualTo("not original condition name")
        }

    @Test
    fun actualValue() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val webElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`<Any?>(
            originalCondition.actualValue(
                any(), any()
            )
        )
            .thenReturn("original condition actual value")
        Assertions.assertThat<Any?>(notCondition!!.actualValue(driver, webElement))
            .isEqualTo("original condition actual value")
        Mockito.verify(originalCondition).actualValue(driver, webElement)
    }

    @Test
    fun applyFalse() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val webElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`<Any>(
            originalCondition.apply(
                any(), any()
            )
        ).thenReturn(true)
        Assertions.assertThat(notCondition!!.apply(driver, webElement)).isFalse
        Mockito.verify(originalCondition).apply(driver, webElement)
    }

    @Test
    fun applyTrue() = runBlockingTest {
        val driver = Mockito.mock(Driver::class.java)
        val webElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`<Any>(
            originalCondition.apply(
                any(), any()
            )
        ).thenReturn(false)
        Assertions.assertThat(notCondition!!.apply(driver, webElement)).isTrue
        Mockito.verify(originalCondition).apply(driver, webElement)
    }

    @Test
    fun hasToString() {
        Mockito.`when`(originalCondition.toString()).thenReturn("original condition toString")
        Assertions.assertThat(notCondition).hasToString("not original condition toString")
    }
}

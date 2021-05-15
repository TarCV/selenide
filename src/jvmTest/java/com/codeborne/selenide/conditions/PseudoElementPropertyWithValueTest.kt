package com.codeborne.selenide.conditions

import com.codeborne.selenide.Driver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class PseudoElementPropertyWithValueTest {
    private val driver = Mockito.mock(Driver::class.java)
    private val element = Mockito.mock(WebElement::class.java)
    @BeforeEach
    fun setUp() = runBlockingTest {
        Mockito.`when`(
            driver.executeJavaScript<Any>(
                PseudoElementPropertyWithValue.JS_CODE,
                element,
                ":before",
                "content"
            )
        ).thenReturn("hello")
    }

    @Test
    fun apply() = runBlockingTest {
        Assertions.assertThat(
            PseudoElementPropertyWithValue(":before", "content", "hello")
                .apply(driver, element)
        ).isTrue
        Assertions.assertThat(
            PseudoElementPropertyWithValue(":before", "content", "Hello")
                .apply(driver, element)
        ).isTrue
        Assertions.assertThat(
            PseudoElementPropertyWithValue(":before", "content", "dummy")
                .apply(driver, element)
        ).isFalse()
    }

    @Test
    fun actualValue() = runBlockingTest {
        Assertions.assertThat<Any>(
            PseudoElementPropertyWithValue(":before", "content", "hello")
                .actualValue(driver, element)
        ).isEqualTo(":before {content: hello;}")
    }

    @Test
    fun tostring() {
        Assertions.assertThat(PseudoElementPropertyWithValue(":before", "content", "hello"))
            .hasToString("pseudo-element :before {content: hello;}")
    }
}

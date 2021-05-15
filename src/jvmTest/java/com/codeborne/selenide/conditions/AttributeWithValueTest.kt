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
internal class AttributeWithValueTest {
    private val driver = Mockito.mock(Driver::class.java)
    private val element = Mockito.mock(WebElement::class.java)
    @BeforeEach
    fun setUp() {
        Mockito.`when`(element.getAttribute("data-id")).thenReturn("actual")
    }

    @Test
    fun apply() = runBlockingTest {
        Assertions.assertThat(AttributeWithValue("data-id", "actual").apply(driver, element)).isTrue
        Assertions.assertThat(AttributeWithValue("data-id", "expected").apply(driver, element)).isFalse
        Assertions.assertThat(MatchAttributeWithValue("data-id", "act.*").apply(driver, element)).isTrue
        Assertions.assertThat(MatchAttributeWithValue("data-id", "exp.*").apply(driver, element)).isFalse
    }

    @Test
    fun actualValue() = runBlockingTest {
        Assertions.assertThat(AttributeWithValue("data-id", "expected").actualValue(driver, element))
            .isEqualTo("data-id=\"actual\"")
        Assertions.assertThat(MatchAttributeWithValue("data-id", "expected").actualValue(driver, element))
            .isEqualTo("data-id=\"actual\"")
    }

    @Test
    fun tostring() {
        Assertions.assertThat(AttributeWithValue("data-id", "expected")).hasToString("attribute data-id=\"expected\"")
        Assertions.assertThat(MatchAttributeWithValue("data-id", "exp.*"))
            .hasToString("match attribute data-id=\"exp.*\"")
    }
}

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
internal class ExactTextCaseSensitiveTest {
    private val driver = Mockito.mock(Driver::class.java)
    private val element = Mockito.mock(WebElement::class.java)
    @BeforeEach
    fun setUp() {
        Mockito.`when`(element.text).thenReturn("One")
    }

    @Test
    fun shouldMatchExpectedTextWithSameCase() = runBlockingTest {
        Assertions.assertThat<Any>(ExactTextCaseSensitive("One").apply(driver, element)).isEqualTo(true)
    }

    @Test
    fun shouldNotMatchExpectedTextWithDifferentCase() = runBlockingTest {
        Assertions.assertThat<Any>(ExactTextCaseSensitive("one").apply(driver, element)).isEqualTo(false)
    }

    @Test
    fun shouldNotMatchDifferentExpectedText() = runBlockingTest {
        Assertions.assertThat<Any>(ExactTextCaseSensitive("Two").apply(driver, element)).isEqualTo(false)
    }

    @Test
    fun shouldHaveCorrectToString() {
        Assertions.assertThat(ExactTextCaseSensitive("One")).hasToString("exact text case sensitive 'One'")
    }
}

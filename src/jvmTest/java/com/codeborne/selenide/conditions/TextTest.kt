package com.codeborne.selenide.conditions

import com.codeborne.selenide.Driver
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.util.stream.Collectors
import java.util.stream.Stream

internal class TextTest : WithAssertions {
    private val driver = Mockito.mock(Driver::class.java)
    @Test
    fun apply_for_textInput() = runBlockingTest {
        assertThat(Text("Hello World").apply(driver, elementWithText("Hello World"))).isTrue
        assertThat(Text("Hello World").apply(driver, elementWithText("Hello"))).isFalse()
    }

    @Test
    fun apply_matchTextPartially() = runBlockingTest {
        assertThat(Text("Hello").apply(driver, elementWithText("Hello World"))).isTrue
        assertThat(Text("World").apply(driver, elementWithText("Hello World"))).isTrue()
    }

    @Test
    fun apply_for_select() = runBlockingTest {
        assertThat(Text("Hello World").apply(driver, select("Hello", "World"))).isFalse
        assertThat(Text("Hello World").apply(driver, select("Hello", " World"))).isTrue()
    }

    @Test
    fun to_string() {
        assertThat(Text("Hello World")).hasToString("text 'Hello World'")
    }

    @Test
    fun negate_to_string() {
        assertThat(Text("Hello World").negate()).hasToString("not text 'Hello World'")
    }

    private fun elementWithText(text: String): WebElement {
        val webElement = Mockito.mock(WebElement::class.java)
        Mockito.`when`(webElement.text).thenReturn(text)
        return webElement
    }

    private fun select(vararg optionTexts: String): WebElement {
        val select = elementWithText("Hello World")
        Mockito.`when`(select.tagName).thenReturn("select")
        val options = Stream.of(*optionTexts)
            .map { text: String -> elementWithText(text) }
            .peek { option: WebElement -> Mockito.`when`(option.isSelected).thenReturn(true) }
            .collect(Collectors.toList())
        Mockito.`when`(select.findElements(By.tagName("option"))).thenReturn(options)
        return select
    }
}

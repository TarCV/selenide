package com.codeborne.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class SelectOptionContainingTextTest : WithAssertions {
    private val command = SelectOptionContainingText()
    private val element = Mockito.mock(WebElement::class.java)
    private val option1 = Mockito.mock(WebElement::class.java)
    private val option2 = Mockito.mock(WebElement::class.java)
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val select = Mockito.mock(WebElementSource::class.java)
    @BeforeEach
    fun setUp() = runBlockingTest {
        Mockito.doReturn(element).`when`(select).getWebElement()
        Mockito.doReturn("select").`when`(element).tagName
    }

    @Test
    fun selectsFirstMatchingOptionForSingleSelect() = runBlockingTest {
        Mockito.doReturn("false").`when`(element).getAttribute("multiple")
        Mockito.doReturn(Arrays.asList(option1, option2)).`when`(element)
            .findElements(
                By.xpath(".//option[contains(normalize-space(.), \"option-subtext\")]")
            )
        command.execute(proxy, select, arrayOf<Any>("option-subtext"))
        Mockito.verify(option1).click()
        Mockito.verify(option2, Mockito.never()).click()
    }

    @Test
    fun selectsAllMatchingOptionsForMultipleSelect() = runBlockingTest {
        Mockito.doReturn("true").`when`(element).getAttribute("multiple")
        Mockito.doReturn(Arrays.asList(option1, option2)).`when`(element)
            .findElements(
                By.xpath(".//option[contains(normalize-space(.), \"option-subtext\")]")
            )
        command.execute(proxy, select, arrayOf<Any>("option-subtext"))
        Mockito.verify(option1).click()
        Mockito.verify(option2).click()
    }

    @Test
    fun throwsNoSuchElementExceptionWhenNoElementsFound() = runBlockingTest {
        val elementText = "option-subtext"
        try {
            command.execute(proxy, select, arrayOf<Any>(elementText))
        } catch (exception: NoSuchElementException) {
            assertThat(exception)
                .hasMessageContaining(String.format("Cannot locate option containing text: %s", elementText))
        }
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
internal class GetTextCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val getSelectedTextCommand = Mockito.mock(GetSelectedText::class.java)
    private val getTextCommand = GetText(getSelectedTextCommand)
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
    }

    @Test
    @Throws(IllegalAccessException::class, NoSuchFieldException::class)
    fun testDefaultConstructor() {
        val getText = GetText()
        val getSelectedTextField = getText.javaClass.getDeclaredField("getSelectedText")
        getSelectedTextField.isAccessible = true
        val getSelectedText = getSelectedTextField[getText] as GetSelectedText
        assertThat(getSelectedText)
            .isNotNull
    }

    @Test
    fun testExecuteMethodWithSelectElement() = runBlockingTest {
        Mockito.`when`(mockedElement.tagName).thenReturn("select")
        val args = arrayOf<Any>("something more")
        val selectedElementText = "Selected Text"
        Mockito.`when`<Any>(getSelectedTextCommand.execute(proxy, locator, args)).thenReturn(selectedElementText)
        assertThat<Any>(getTextCommand.execute(proxy, locator, args))
            .isEqualTo(selectedElementText)
    }

    @Test
    fun testExecuteMethodWithNotSelectElement() = runBlockingTest {
        Mockito.`when`(mockedElement.tagName).thenReturn("href")
        val text = "This is text"
        Mockito.`when`(mockedElement.text).thenReturn(text)
        assertThat<Any>(getTextCommand.execute(proxy, locator, arrayOf<Any>("something more")))
            .isEqualTo(text)
    }
}

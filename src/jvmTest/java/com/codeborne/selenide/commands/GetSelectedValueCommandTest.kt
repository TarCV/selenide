package com.codeborne.selenide.commands

import assertk.assertions.isNullOrEmpty
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.io.IOException

@ExperimentalCoroutinesApi
internal class GetSelectedValueCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val selectElement = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private var getSelectedValueCommand: GetSelectedValue? = null
    private val getSelectedOptionCommand = Mockito.mock(GetSelectedOption::class.java)
    @BeforeEach
    fun setup() {
        getSelectedValueCommand = GetSelectedValue(getSelectedOptionCommand)
    }

    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun testDefaultConstructor() {
        val getSelectedText = GetSelectedValue()
        val getSelectedOptionField = getSelectedText.javaClass.getDeclaredField("getSelectedOption")
        getSelectedOptionField.isAccessible = true
        val getSelectedOption = getSelectedOptionField[getSelectedText] as GetSelectedOption
        assertThat(getSelectedOption)
            .isNotNull
    }

    @Test
    @Throws(IOException::class)
    fun testExecuteMethodWhenSelectedOptionReturnsNothing() = runBlockingTest {
        val args = arrayOf<Any>("something more")
        Mockito.`when`<Any?>(getSelectedOptionCommand.execute(proxy, selectElement, args)).thenReturn(null)
        assertk.assertThat(getSelectedValueCommand!!.execute(proxy, selectElement, args))
            .isNullOrEmpty()
    }

    @Test
    @Throws(IOException::class)
    fun testExecuteMethodWhenSelectedOptionReturnsElement() = runBlockingTest {
        val args = arrayOf<Any>("something more")
        Mockito.`when`<Any>(getSelectedOptionCommand.execute(proxy, selectElement, args)).thenReturn(mockedElement)
        val elementText = "Element text"
        Mockito.`when`(mockedElement.getAttribute("value")).thenReturn(elementText)
        assertThat<Any>(getSelectedValueCommand!!.execute(proxy, selectElement, args))
            .isEqualTo(elementText)
    }
}

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
internal class GetValueCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val getValueCommand = GetValue()
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
    }

    @Test
    fun testExecuteMethod() = runBlockingTest {
        val argument = "class"
        val elementAttribute = "hello"
        Mockito.`when`(mockedElement.getAttribute("value")).thenReturn(elementAttribute)
        assertThat<Any>(getValueCommand.execute(proxy, locator, arrayOf<Any>(argument, "something more")))
            .isEqualTo(elementAttribute)
    }
}

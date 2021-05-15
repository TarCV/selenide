package com.codeborne.selenide.commands

import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
internal class GetInnerTextCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val getInnerTextCommand = GetInnerText()
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
    }

    @Test
    fun uses_textContent_attribute_if_not_IE() = runBlockingTest {
        Mockito.`when`(locator.driver()).thenReturn(DriverStub("firefox"))
        Mockito.`when`(mockedElement.getAttribute("textContent")).thenReturn("hello")
        assertThat<Any>(getInnerTextCommand.execute(proxy, locator, arrayOf())).isEqualTo("hello")
    }
}

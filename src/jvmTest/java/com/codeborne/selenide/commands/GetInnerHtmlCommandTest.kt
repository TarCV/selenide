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
internal class GetInnerHtmlCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val getInnerHtmlCommand = GetInnerHtml()
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
    }

    @Test
    fun uses_innerHTML_attribute() = runBlockingTest {
        Mockito.`when`(locator.driver()).thenReturn(DriverStub("firefox"))
        Mockito.`when`(mockedElement.getAttribute("innerHTML")).thenReturn("hello")
        assertThat<Any>(getInnerHtmlCommand.execute(proxy, locator, arrayOf())).isEqualTo("hello")
    }
}

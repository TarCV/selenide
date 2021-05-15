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
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import java.util.Arrays

@ExperimentalCoroutinesApi
internal class GetSelectedOptionCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement1Text = "Element text2"
    private val getSelectedOptionCommand = GetSelectedOption()
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`(locator.driver()).thenReturn(DriverStub())
        val mockedElement = Mockito.mock(SelenideElement::class.java)
        val mockedElement1 = Mockito.mock(WebElement::class.java)
        val mockedElement2 = Mockito.mock(WebElement::class.java)
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
        Mockito.`when`(mockedElement.isSelected).thenReturn(true)
        Mockito.`when`(mockedElement.tagName).thenReturn("select")
        Mockito.`when`(mockedElement.findElements(By.tagName("option")))
            .thenReturn(Arrays.asList(mockedElement1, mockedElement2))
        Mockito.`when`(mockedElement1.isSelected).thenReturn(true)
        Mockito.`when`(mockedElement1.text).thenReturn(mockedElement1Text)
        Mockito.`when`(mockedElement2.isSelected).thenReturn(false)
    }

    @Test
    fun testExecuteMethod() = runBlockingTest {
        val selectedElement = getSelectedOptionCommand.execute(proxy, locator, arrayOf<Any>("something more"))
        assertThat(selectedElement.text)
            .isEqualTo(mockedElement1Text)
    }
}

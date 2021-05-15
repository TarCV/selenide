package com.codeborne.selenide.commands

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class GetPseudoValueCommandTest : WithAssertions {
    private val driver = Mockito.mock(Driver::class.java)
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val element = Mockito.mock(WebElement::class.java)
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`(locator.driver()).thenReturn(driver)
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(element)
    }

    @Test
    fun execute() = runBlockingTest {
        Mockito.`when`(
            driver.executeJavaScript<Any>(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
            )
        ).thenReturn("hello")
        assertThat<Any>(GetPseudoValue().execute(proxy, locator, arrayOf<Any>(":before", "content")))
            .isEqualTo("hello")
        Mockito.verify(driver).executeJavaScript<Any>(GetPseudoValue.JS_CODE, element, ":before", "content")
    }
}

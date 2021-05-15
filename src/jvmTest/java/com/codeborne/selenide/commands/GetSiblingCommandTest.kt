package com.codeborne.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
internal class GetSiblingCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val getSiblingCommand = GetSibling()
    @Test
    fun executeMethod() = runBlockingTest {
        Mockito.`when`(locator.find(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt()))
            .thenReturn(mockedElement)
        assertThat<Any>(getSiblingCommand.execute(proxy, locator, arrayOf<Any>(0))).isEqualTo(mockedElement)
        Mockito.verify(locator).find(proxy, By.xpath("following-sibling::*[1]"), 0)
    }
}

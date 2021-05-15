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
internal class GetPrecedingCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val getPrecedingCommand = GetPreceding()
    @Test
    fun testExecuteMethod() = runBlockingTest {
        Mockito.`when`(locator.find(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt()))
            .thenReturn(mockedElement)
        assertThat<Any>(getPrecedingCommand.execute(proxy, locator, arrayOf<Any>(0))).isEqualTo(mockedElement)
        Mockito.verify(locator).find(proxy, By.xpath("preceding-sibling::*[1]"), 0)
    }
}

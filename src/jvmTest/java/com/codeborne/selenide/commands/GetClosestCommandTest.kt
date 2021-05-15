package com.codeborne.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
internal class GetClosestCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val getClosestCommand = GetClosest()
    @Test
    fun testExecuteMethodWithTagsStartsWithDot() = runBlockingTest {
        val argument = ".class"
        val elementAttribute = "hello"
        Mockito.`when`(mockedElement.getAttribute(argument)).thenReturn(elementAttribute)
        Mockito.`when`(
            locator.find(
                proxy,
                By.xpath(
                    String.format(
                        "ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' %s ')][1]",
                        argument.substring(1)
                    )
                ),
                0
            )
        ).thenReturn(mockedElement)
        assertThat<Any>(getClosestCommand.execute(proxy, locator, arrayOf<Any>(argument, "something more")))
            .isEqualTo(mockedElement)
    }

    @Test
    fun testExecuteMethodWithTagsThatDontStartsWithDot() = runBlockingTest {
        val argument = "class"
        val elementAttribute = "hello"
        Mockito.`when`(mockedElement.getAttribute(argument)).thenReturn(elementAttribute)
        Mockito.`when`(locator.find(proxy, By.xpath(String.format("ancestor::%s[1]", argument)), 0))
            .thenReturn(mockedElement)
        assertThat<Any>(getClosestCommand.execute(proxy, locator, arrayOf<Any>(argument, "something more")))
            .isEqualTo(mockedElement)
    }
}

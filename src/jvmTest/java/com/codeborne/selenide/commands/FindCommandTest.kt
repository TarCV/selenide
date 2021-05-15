package com.codeborne.selenide.commands

import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
internal class FindCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val element1 = Mockito.mock(SelenideElement::class.java)
    private val findCommand = Find()
    @Test
    fun testExecuteMethodWithNoArgsPassed() = runBlockingTest {
        assertk.assertThat { findCommand.execute(proxy, locator, arrayOf()) }
            .isFailure()
            .isInstanceOf(ArrayIndexOutOfBoundsException::class.java)
    }

    @Test
    fun testExecuteMethodWithZeroLengthArgs() = runBlockingTest {
        Mockito.`when`(locator.find(proxy, By.xpath(".."), 0)).thenReturn(element1)
        assertThat<Any>(findCommand.execute(proxy, locator, arrayOf<Any>(By.xpath(".."))))
            .isEqualTo(element1)
    }

    @Test
    fun testExecuteMethodWithMoreThenOneArgsList() = runBlockingTest {
        Mockito.`when`(locator.find(proxy, By.xpath(".."), 1)).thenReturn(element1)
        assertThat<Any>(findCommand.execute(proxy, locator, arrayOf(By.xpath(".."), 1)))
            .isEqualTo(element1)
    }
}

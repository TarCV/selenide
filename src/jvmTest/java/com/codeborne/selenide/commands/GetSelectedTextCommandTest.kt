package com.codeborne.selenide.commands

import com.codeborne.selenide.Mocks.mockElement
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.SelenideElementProxy.Companion.NO_ARGS
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
internal class GetSelectedTextCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val selectElement = Mockito.mock(WebElementSource::class.java)
    private val getSelectedOptionCommand = Mockito.mock(GetSelectedOption::class.java)
    private val command = GetSelectedText(getSelectedOptionCommand)
    @Test
    fun returnsTextOfSelectedOption() = runBlockingTest {
        val option = mockElement("option", "Option text")
        Mockito.`when`<Any>(
            getSelectedOptionCommand.execute(
                any(),
                any(),
                any()
            )
        ).thenReturn(option)
        assertThat<Any>(command.execute(proxy, selectElement, arrayOf())).isEqualTo("Option text")
        Mockito.verify(getSelectedOptionCommand).execute(proxy, selectElement, NO_ARGS)
    }
}

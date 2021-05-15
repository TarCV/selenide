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
internal class GetSearchCriteriaCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val getSearchCriteriaCommand = GetSearchCriteria()
    private val defaultSearchCriteria = "by.xpath"
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getSearchCriteria()).thenReturn(defaultSearchCriteria)
    }

    @Test
    fun testExecuteMethod() = runBlockingTest {
        assertThat<Any>(getSearchCriteriaCommand.execute(proxy, locator, arrayOf<Any>("something more")))
            .isEqualTo(defaultSearchCriteria)
    }
}

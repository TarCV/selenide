package com.codeborne.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.Alias
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class GetAliasCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val getAlias = GetAlias()
    @Test
    fun returns_element_alias_if_defined() = runBlockingTest {
        Mockito.`when`(locator.alias).thenReturn(Alias("my element"))
        assertThat<Any>(getAlias.execute(proxy, locator, arrayOf()))
            .`as`("should return element alias")
            .isEqualTo("my element")
    }

    @Test
    fun returns_null_ifAliasNotSet() = runBlockingTest {
        Mockito.`when`(locator.alias).thenReturn(Alias.NONE)
        assertThat<Any>(getAlias.execute(proxy, locator, arrayOf()))
            .`as`("should return null when alias is not set")
            .isNull()
    }
}

package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
internal class ValCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedGetValue = Mockito.mock(GetValue::class.java)
    private val mockedSetValue = Mockito.mock(SetValue::class.java)
    private val valCommand = Val(mockedGetValue, mockedSetValue)
    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun testDefaultConstructor() {
        val `val` = Val()
        val getValueField = `val`.javaClass.getDeclaredField("getValue")
        val setValueField = `val`.javaClass.getDeclaredField("setValue")
        getValueField.isAccessible = true
        setValueField.isAccessible = true
        val getValue = getValueField[`val`] as GetValue
        val setValue = setValueField[`val`] as SetValue
        assertThat(getValue)
            .isNotNull
        assertThat(setValue)
            .isNotNull
    }

    @Test
    fun testExecuteValueWithNoArgs() = runBlockingTest {
        val getValueResult = "getValueResult"
        Mockito.`when`<Any?>(mockedGetValue.execute(proxy, locator, Command.NO_ARGS)).thenReturn(getValueResult)
        assertThat(valCommand.execute(proxy, locator, arrayOf()))
            .isEqualTo(getValueResult)
        assertThat(valCommand.execute(proxy, locator, arrayOf()))
            .isEqualTo(getValueResult)
    }

    @Test
    fun testExecuteValueWithArgs() = runBlockingTest {
        assertThat(valCommand.execute(proxy, locator, arrayOf<Any>("value")))
            .isEqualTo(proxy)
    }
}

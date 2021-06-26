package com.codeborne.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.SelenideElementProxy.Companion.NO_ARGS
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
internal class GetValCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedGetValue = Mockito.mock(GetValue::class.java)
    private val mockedSetValue = Mockito.mock(SetValue::class.java)
    private val getValCommand = GetVal(mockedGetValue)
    private val setValCommand = SetVal(mockedSetValue)
    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun testGetDefaultConstructor() {
        val `val` = GetVal()
        val getValueField = `val`.javaClass.getDeclaredField("getValue")
        getValueField.isAccessible = true
        val getValue = getValueField[`val`] as GetValue
        assertThat(getValue)
            .isNotNull
    }

    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun testSetDefaultConstructor() {
        val `val` = SetVal()
        val setValueField = `val`.javaClass.getDeclaredField("setValue")
        setValueField.isAccessible = true
        val setValue = setValueField[`val`] as SetValue
        assertThat(setValue)
            .isNotNull
    }

    @Test
    fun testExecuteValueWithNoArgs() = runBlockingTest {
        val getValueResult = "getValueResult"
        Mockito.`when`<Any?>(mockedGetValue.execute(proxy, locator, NO_ARGS)).thenReturn(getValueResult)
        assertThat(getValCommand.execute(proxy, locator, arrayOf()))
            .isEqualTo(getValueResult)
        assertThat(getValCommand.execute(proxy, locator, arrayOf()))
            .isEqualTo(getValueResult)
    }

    @Test
    fun testExecuteValueWithArgs() = runBlockingTest {
        assertThat(setValCommand.execute(proxy, locator, arrayOf<Any>("value")))
            .isEqualTo(proxy)
    }
}

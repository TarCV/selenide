package com.codeborne.selenide.commands

import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class SetValueCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedSelectByOption = Mockito.mock(SelectOptionByValue::class.java)
    private val mockedSelectRadio = Mockito.mock(SelectRadio::class.java)
    private val setValueCommand = SetValue(mockedSelectByOption, mockedSelectRadio)
    private val mockedFoundElement = Mockito.mock(WebElement::class.java)
    @BeforeEach
    fun setup() = runBlockingTest {
        System.setProperty("selenide.versatileSetValue", "true")
        Mockito.`when`<Any>(locator.findAndAssertElementIsInteractable()).thenReturn(mockedFoundElement)
        Mockito.`when`(locator.driver()).thenReturn(DriverStub())
    }

    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun testDefaultConstructor() {
        val setValue = SetValue()
        val selectOptionByValueField = setValue.javaClass.getDeclaredField("selectOptionByValue")
        val selectRadioField = setValue.javaClass.getDeclaredField("selectRadio")
        selectOptionByValueField.isAccessible = true
        selectRadioField.isAccessible = true
        val selectOptionByValue = selectOptionByValueField[setValue] as SelectOptionByValue
        val selectRadio = selectRadioField[setValue] as SelectRadio
        assertThat(selectOptionByValue)
            .isNotNull
        assertThat(selectRadio)
            .isNotNull
    }

    @Test
    fun testExecuteWithSelectTagElement() = runBlockingTest {
        System.setProperty("selenide.versatileSetValue", "true")
        Mockito.`when`(mockedFoundElement.tagName).thenReturn("select")
        val returnedElement: WebElement = setValueCommand.execute(proxy, locator, arrayOf<Any>("value"))
        assertThat(returnedElement)
            .isEqualTo(proxy)
    }

    @Test
    fun testExecuteWithInputTagElement() = runBlockingTest {
        Mockito.`when`(mockedFoundElement.tagName).thenReturn("input")
        Mockito.`when`(mockedFoundElement.getAttribute("type")).thenReturn("radio")
        val returnedElement: WebElement = setValueCommand.execute(proxy, locator, arrayOf<Any>("value"))
        assertThat(returnedElement)
            .isEqualTo(proxy)
    }

    @Test
    fun testElementGetClearedWhenArgsTextIsNull() = runBlockingTest {
        val returnedElement: WebElement = setValueCommand.execute(proxy, locator, arrayOf())
        assertThat(returnedElement)
            .isEqualTo(proxy)
    }

    @Test
    fun testElementGetClearedWhenArgsTextIsEmpty() = runBlockingTest {
        val returnedElement: WebElement = setValueCommand.execute(proxy, locator, arrayOf<Any>(""))
        assertThat(returnedElement)
            .isEqualTo(proxy)
    }

    @Test
    fun testElementGetClearedWhenArgsTextIsNotEmpty() = runBlockingTest {
        val returnedElement: WebElement = setValueCommand.execute(proxy, locator, arrayOf<Any>("text"))
        assertThat(returnedElement)
            .isEqualTo(proxy)
    }

    @AfterEach
    fun tearDown() {
        System.setProperty("selenide.versatileSetValue", "false")
    }
}

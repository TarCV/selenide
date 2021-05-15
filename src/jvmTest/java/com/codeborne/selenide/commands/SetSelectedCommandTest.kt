package com.codeborne.selenide.commands

import assertk.all
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.startsWith
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.InvalidStateException
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class SetSelectedCommandTest : WithAssertions {
    private val mockedClick = Mockito.mock(Click::class.java)
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val setSelectedCommand = SetSelected(mockedClick)
    private val mockedFoundElement = Mockito.mock(WebElement::class.java)
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`(locator.driver()).thenReturn(DriverStub())
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedFoundElement)
    }

    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun defaultConstructor() {
        val setSelected = SetSelected()
        val clickField = setSelected.javaClass.getDeclaredField("click")
        clickField.isAccessible = true
        val click = clickField[setSelected] as Click
        assertThat(click)
            .isNotNull
    }

    @Test
    fun executeMethodWhenElementIsNotDisplayed() = runBlockingTest {
        Mockito.`when`(mockedFoundElement.isDisplayed).thenReturn(false)
        try {
            setSelectedCommand.execute(proxy, locator, arrayOf<Any>(true))
        } catch (exception: InvalidStateException) {
            assertk.assertThat(exception)
                .message()
                .isNotNull()
                .all {
                    startsWith("Invalid element state: Cannot change invisible element")
                }
        }
    }

    @Test
    fun executeMethodWhenElementIsNotInput() {
        checkExecuteMethodWhenTypeOfElementIsIncorrect("select")
    }

    private fun checkExecuteMethodWhenTypeOfElementIsIncorrect(tagName: String) = runBlockingTest {
        Mockito.`when`(mockedFoundElement.isDisplayed).thenReturn(true)
        Mockito.`when`(mockedFoundElement.tagName).thenReturn(tagName)
        Mockito.`when`(mockedFoundElement.getAttribute("type")).thenReturn("href")
        try {
            setSelectedCommand.execute(proxy, locator, arrayOf<Any>(true))
        } catch (exception: InvalidStateException) {
            assertk.assertThat(exception)
                .message()
                .isNotNull()
                .all {
                    startsWith("Invalid element state: Only use setSelected on checkbox/option/radio")
                }
        }
    }

    @Test
    fun executeMethodWhenElementIsInputNotRadioOrCheckbox() {
        checkExecuteMethodWhenTypeOfElementIsIncorrect("input")
    }

    @Test
    fun executeMethodWhenElementNotOptionReadonlyEnabled() {
        checkExecuteMethodWhenElementIsReadOnlyOrDisabled("true", null)
    }

    private fun checkExecuteMethodWhenElementIsReadOnlyOrDisabled(readOnlyValue: String?, disabledValue: String?) = runBlockingTest {
        Mockito.`when`(mockedFoundElement.isDisplayed).thenReturn(true)
        Mockito.`when`(mockedFoundElement.tagName).thenReturn("option")
        Mockito.`when`(mockedFoundElement.getAttribute("readonly")).thenReturn(readOnlyValue)
        Mockito.`when`(mockedFoundElement.getAttribute("disabled")).thenReturn(disabledValue)
        assertk.assertThat { setSelectedCommand.execute(proxy, locator, arrayOf<Any>(true)) }
            .isFailure()
            .isInstanceOf(InvalidStateException::class.java)
            .message()
            .isNotNull()
            .all {
                startsWith("Invalid element state: Cannot change value of readonly/disabled element")
            }
    }

    @Test
    fun executeMethodWhenElementNotOptionNotReadonlyDisabled() {
        checkExecuteMethodWhenElementIsReadOnlyOrDisabled(null, "true")
    }

    @Test
    fun executeMethodWhenElementNotOptionReadonlyDisabled() {
        checkExecuteMethodWhenElementIsReadOnlyOrDisabled("true", "true")
    }

    @Test
    fun executeMethodWhenElementIsSelected() = runBlockingTest {
        Mockito.`when`(mockedFoundElement.isDisplayed).thenReturn(true)
        Mockito.`when`(mockedFoundElement.tagName).thenReturn("option")
        val returnedElement: WebElement = setSelectedCommand.execute(proxy, locator, arrayOf<Any>(true))
        assertThat(returnedElement)
            .isEqualTo(proxy)
    }
}

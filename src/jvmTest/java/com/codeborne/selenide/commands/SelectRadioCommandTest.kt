package com.codeborne.selenide.commands

import assertk.all
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.startsWith
import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
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
internal class SelectRadioCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val selectRadioCommand = SelectRadio(Mockito.mock(Click::class.java))
    private val mockedFoundElement = Mockito.mock(WebElement::class.java)
    private val defaultElementValue = "ElementValue"
    @BeforeEach
    fun setup() {
        Mockito.`when`(locator.driver()).thenReturn(DriverStub())
        Mockito.`when`(mockedFoundElement.getAttribute("value")).thenReturn(defaultElementValue)
    }

    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun testDefaultConstructor() {
        val selectRadio = SelectRadio()
        val clickField = selectRadio.javaClass.getDeclaredField("click")
        clickField.isAccessible = true
        val click = clickField[selectRadio] as Click
        assertThat(click)
            .isNotNull
    }

    @Test
    fun testExecuteMethodWhenNoElementsIsFound() = runBlockingTest {
        Mockito.`when`<Any>(locator.findAll()).thenReturn(emptyList<Any>())
        try {
            selectRadioCommand.execute(proxy, locator, arrayOf<Any>(defaultElementValue))
        } catch (exception: ElementNotFound) {
            assertk.assertThat(exception)
                .message()
                .isNotNull()
                .all {
                    startsWith(
                        String.format(
                            "Element not found {null}%nExpected: value '%s'",
                            defaultElementValue
                        )
                    )
                }
        }
    }

    @Test
    fun testExecuteMethodWhenRadioButtonIsReadOnly() = runBlockingTest {
        Mockito.`when`<Any>(locator.findAll()).thenReturn(listOf(mockedFoundElement))
        Mockito.`when`(mockedFoundElement.getAttribute("readonly")).thenReturn("true")
        try {
            selectRadioCommand.execute(proxy, locator, arrayOf<Any>(defaultElementValue))
        } catch (exception: InvalidStateException) {
            assertk.assertThat(exception)
                .message()
                .isNotNull()
                .all {
                    startsWith("Invalid element state: Cannot select readonly radio button")
                }
        }
    }

    @Test
    fun testExecuteMethodOnFoundRadioButton() = runBlockingTest {
        Mockito.`when`<Any>(locator.findAll()).thenReturn(listOf(mockedFoundElement))
        val clickedElement = selectRadioCommand.execute(proxy, locator, arrayOf<Any>(defaultElementValue))
        assertThat(clickedElement.wrappedElement)
            .isEqualTo(mockedFoundElement)
    }
}

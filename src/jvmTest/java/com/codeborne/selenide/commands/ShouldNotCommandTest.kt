package com.codeborne.selenide.commands

import com.codeborne.selenide.Condition
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.WebElement
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
internal class ShouldNotCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val shouldNotCommand = ShouldNot()
    private val mockedFoundElement = Mockito.mock(WebElement::class.java)
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedFoundElement)
    }

    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun testDefaultConstructor() {
        val shouldNot = ShouldNot()
        val prefixField = shouldNot.javaClass.getDeclaredField("prefix")
        prefixField.isAccessible = true
        val prefix = prefixField[shouldNot] as String
        assertThat(prefix)
            .isNullOrEmpty()
    }

    @Test
    fun testExecuteMethodWithNonStringArgs() = runBlockingTest {
        val returnedElement = shouldNotCommand.execute(proxy, locator, arrayOf<Any>(Condition.disabled))
        assertThat(returnedElement)
            .isEqualTo(proxy)
    }

    @Test
    fun testExecuteMethodWithStringArgs() = runBlockingTest {
        val returnedElement = shouldNotCommand.execute(proxy, locator, arrayOf<Any>("hello"))
        assertThat(returnedElement)
            .isEqualTo(proxy)
    }
}
package com.codeborne.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
internal class GetParentCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val findMock = Mockito.mock(
        Find::class.java
    )
    private val getParentCommand = GetParent(findMock)
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
        Mockito.`when`<Any>(findMock.execute(proxy, locator, arrayOf(By.xpath(".."), 0))).thenReturn(mockedElement)
    }

    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun testDefaultConstructor() {
        val getParentCommand = GetParent()
        val findField = getParentCommand.javaClass.getDeclaredField("find")
        findField.isAccessible = true
        val find = findField[getParentCommand] as Find
        assertThat(find)
            .isNotNull
    }

    @Test
    fun testExecuteMethod() = runBlockingTest {
        assertThat<Any>(getParentCommand.execute(proxy, locator, arrayOf<Any>("..", "something more")))
            .isEqualTo(mockedElement)
    }
}

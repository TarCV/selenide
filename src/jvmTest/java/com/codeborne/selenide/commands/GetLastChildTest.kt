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
internal class GetLastChildTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val findMock = Mockito.mock(
        Find::class.java
    )
    private val getLastChildCommand = GetLastChild(findMock)
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
        Mockito.`when`<Any>(findMock.execute(proxy, locator, arrayOf(By.xpath("*[last()]"), 0)))
            .thenReturn(mockedElement)
    }

    @Test
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun testDefaultConstructor() {
        val getLastChild = GetLastChild()
        val findField = getLastChild.javaClass.getDeclaredField("find")
        findField.isAccessible = true
        val find = findField[getLastChild] as Find
        assertThat(find)
            .isNotNull
    }

    @Test
    fun testExecuteMethod() = runBlockingTest {
        assertThat<Any>(getLastChildCommand.execute(proxy, locator, arrayOf<Any>("*[last()]", "something more")))
            .isEqualTo(mockedElement)
    }
}

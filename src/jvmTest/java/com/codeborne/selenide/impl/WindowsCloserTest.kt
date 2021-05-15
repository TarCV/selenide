package com.codeborne.selenide.impl

import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import java.io.IOException
import java.util.Arrays

internal class WindowsCloserTest {
    private val webdriver = Mockito.mock(WebDriver::class.java)
    private val windowsCloser = WindowsCloser()
    @BeforeEach
    fun setUp() {
        Mockito.`when`(webdriver.switchTo()).thenReturn(
            Mockito.mock(
                WebDriver.TargetLocator::class.java
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun closesNewWindowIfFileWasOpenedInSeparateWindow() = runBlockingTest {
        Mockito.`when`(webdriver.windowHandle).thenReturn("tab1")
        Mockito.`when`(webdriver.windowHandles)
            .thenReturn(HashSet(Arrays.asList("tab1", "tab2", "tab3")))
            .thenReturn(HashSet(Arrays.asList("tab1", "tab2", "tab3", "tab-with-pdf")))
        val status = windowsCloser.runAndCloseArisedWindows(webdriver) { "Done" }
        Assertions.assertThat(status).isEqualTo("Done")
        Mockito.verify(webdriver.switchTo()).window("tab-with-pdf")
        Mockito.verify(webdriver).close()
        Mockito.verify(webdriver.switchTo()).window("tab1")
        Mockito.verifyNoMoreInteractions(webdriver.switchTo())
    }

    @Test
    @Throws(IOException::class)
    fun ignoresErrorIfWindowHasAlreadyBeenClosedMeanwhile() = runBlockingTest {
        val targetLocator = Mockito.mock(
            WebDriver.TargetLocator::class.java
        )
        Mockito.doReturn(targetLocator).`when`(webdriver).switchTo()
        Mockito.doThrow(NoSuchWindowException("no window: tab-with-pdf")).`when`(targetLocator).window("tab-with-pdf")
        Mockito.`when`(webdriver.windowHandle).thenReturn("tab1")
        Mockito.`when`(webdriver.windowHandles)
            .thenReturn(HashSet(Arrays.asList("tab1", "tab2", "tab3")))
            .thenReturn(HashSet(Arrays.asList("tab1", "tab2", "tab3", "tab-with-pdf")))
        val status = windowsCloser.runAndCloseArisedWindows(webdriver) { "Done" }
        Assertions.assertThat(status).isEqualTo("Done")
        Mockito.verify(webdriver.switchTo()).window("tab-with-pdf")
        Mockito.verify(webdriver, Mockito.never()).close()
        Mockito.verify(webdriver.switchTo()).window("tab1")
        Mockito.verifyNoMoreInteractions(webdriver.switchTo())
    }
}

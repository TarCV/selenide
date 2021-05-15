package com.codeborne.selenide.ex

import com.codeborne.selenide.SelenideConfig
import com.codeborne.selenide.ex.ErrorMessages.timeout
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.chrome.ChromeDriver
import java.util.Locale

internal class ErrorMessagesTest : WithAssertions {
    private val webDriver = Mockito.mock(ChromeDriver::class.java)
    private val config = SelenideConfig().reportsFolder("build/reports/tests")
    @BeforeEach
    fun setUp() {
        config.screenshots(true)
        config.savePageSource(false)
        Mockito.`when`(webDriver.pageSource).thenReturn("<html></html>")
    }

    @Test
    fun formatsTimeoutToReadable() {
        Locale.setDefault(Locale.UK)
        assertThat(timeout(0))
            .isEqualToIgnoringNewLines("Timeout: 0 ms.")
        assertThat(timeout(1))
            .isEqualToIgnoringNewLines("Timeout: 1 ms.")
        assertThat(timeout(999))
            .isEqualToIgnoringNewLines("Timeout: 999 ms.")
        assertThat(timeout(1000))
            .isEqualToIgnoringNewLines("Timeout: 1 s.")
        assertThat(timeout(1001))
            .isEqualToIgnoringNewLines("Timeout: 1.001 s.")
        assertThat(timeout(1500))
            .isEqualToIgnoringNewLines("Timeout: 1.500 s.")
        assertThat(timeout(4000))
            .isEqualToIgnoringNewLines("Timeout: 4 s.")
    }
}

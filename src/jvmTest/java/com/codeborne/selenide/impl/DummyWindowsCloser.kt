package com.codeborne.selenide.impl;

import org.openqa.selenium.WebDriver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.FileNotFoundException;

@ParametersAreNonnullByDefault
class DummyWindowsCloser: WindowsCloser() {
    override suspend fun <T : Any> runAndCloseArisedWindows(webDriver: WebDriver, lambda: suspend () -> T): T {
        return lambda()
    }
}

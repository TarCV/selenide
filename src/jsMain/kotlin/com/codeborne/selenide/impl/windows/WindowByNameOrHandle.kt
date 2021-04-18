package com.codeborne.selenide.impl.windows

import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition

class WindowByNameOrHandle(private val nameOrHandleOrTitle: String) : ExpectedCondition<WebDriver> {
    override suspend operator fun invoke(driver: WebDriver): WebDriver? {
        checkNotNull(driver)
        return try {
            driver.switchTo().window(nameOrHandleOrTitle)
        } catch (windowWithNameOrHandleNotFound: NoSuchWindowException) {
            try {
                windowByTitle(driver, nameOrHandleOrTitle)
            } catch (e: NoSuchWindowException) {
                null
            }
        }
    }
    override fun toString(): String {
        return "window to be available by name or handle or title: $nameOrHandleOrTitle"
    }

    /**
     * Switch to window/tab by name/handle/title except some windows handles
     * @param title title of window/tab
     */
    private suspend fun windowByTitle(driver: WebDriver, title: String): WebDriver {
        val windowHandles = driver.windowHandles
        for (windowHandle in windowHandles) {
            driver.switchTo().window(windowHandle)
            if (title == driver.title) {
                return driver
            }
        }
        throw NoSuchWindowException("Window with title not found: $title")
    }
}

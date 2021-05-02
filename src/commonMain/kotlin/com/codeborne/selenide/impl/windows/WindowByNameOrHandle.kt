package com.codeborne.selenide.impl.windows

import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import kotlinx.coroutines.runBlocking

class WindowByNameOrHandle(private val nameOrHandleOrTitle: String) : ExpectedCondition<org.openqa.selenium.WebDriver> {
    override fun apply(driver: org.openqa.selenium.WebDriver?): org.openqa.selenium.WebDriver? = runBlocking {
        checkNotNull(driver)
        try {
            driver.switchTo().window(nameOrHandleOrTitle)
        } catch (windowWithNameOrHandleNotFound: org.openqa.selenium.NoSuchWindowException) {
            try {
                windowByTitle(driver, nameOrHandleOrTitle)
            } catch (e: org.openqa.selenium.NoSuchWindowException) {
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
    private suspend fun windowByTitle(driver: org.openqa.selenium.WebDriver, title: String): org.openqa.selenium.WebDriver {
        val windowHandles = driver.windowHandles
        for (windowHandle in windowHandles) {
            driver.switchTo().window(windowHandle)
            if (title == driver.title) {
                return driver
            }
        }
        throw org.openqa.selenium.NoSuchWindowException("Window with title not found: $title")
    }
}

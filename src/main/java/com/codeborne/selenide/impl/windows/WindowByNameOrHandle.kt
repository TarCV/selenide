package com.codeborne.selenide.impl.windows

import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import java.util.Objects
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class WindowByNameOrHandle(private val nameOrHandleOrTitle: String) : ExpectedCondition<WebDriver> {
    override fun apply(driver: WebDriver?): WebDriver? {
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

    @CheckReturnValue
    override fun toString(): String {
        return "window to be available by name or handle or title: $nameOrHandleOrTitle"
    }

    /**
     * Switch to window/tab by name/handle/title except some windows handles
     * @param title title of window/tab
     */
    private fun windowByTitle(driver: WebDriver, title: String): WebDriver {
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

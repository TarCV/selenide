package com.codeborne.selenide.impl.windows

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class WindowByIndex(private val index: Int) : ExpectedCondition<WebDriver> {
    override fun apply(driver: WebDriver?): WebDriver? {
        return try {
            checkNotNull(driver)
            val windowHandles: List<String> = ArrayList(driver.windowHandles)
            driver.switchTo().window(windowHandles[index])
        } catch (windowWithIndexNotFound: IndexOutOfBoundsException) {
            null
        }
    }

    @CheckReturnValue
    override fun toString(): String {
        return "window to be available by index: $index"
    }
}

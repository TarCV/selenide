package com.codeborne.selenide.impl.windows

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition

class WindowByIndex(private val index: Int) : ExpectedCondition<org.openqa.selenium.WebDriver> {
    override fun apply(driver: org.openqa.selenium.WebDriver?): org.openqa.selenium.WebDriver? {
        return try {
            checkNotNull(driver)
            val windowHandles: List<String> = ArrayList(driver.windowHandles)
            driver.switchTo().window(windowHandles[index])
        } catch (windowWithIndexNotFound: IndexOutOfBoundsException) {
            null
        }
    }
    override fun toString(): String {
        return "window to be available by index: $index"
    }
}

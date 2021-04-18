package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.Plugins
import org.openqa.selenium.WebElement

class Focused : Condition("focused") {
    private val describe = Plugins.inject(
        ElementDescriber::class
    )

    private fun getFocusedElement(driver: Driver): WebElement? {
        return driver.executeJavaScript("return document.activeElement")
    }

    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        val focusedElement = getFocusedElement(driver)
        return focusedElement != null && focusedElement == element
    }

    override suspend fun actualValue(driver: Driver, element: WebElement): String {
        val focusedElement = getFocusedElement(driver)
        return if (focusedElement == null) "No focused element found " else "Focused element: " + describe.fully(
            driver,
            focusedElement
        ) +
                ", current element: " + describe.fully(driver, element)
    }
}

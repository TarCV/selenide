package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.Plugins
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Focused : Condition("focused") {
    private val describe = Plugins.inject(
        ElementDescriber::class.java
    )

    private fun getFocusedElement(driver: Driver): WebElement? {
        return driver.executeJavaScript("return document.activeElement")
    }

    override fun apply(driver: Driver, element: WebElement): Boolean {
        val focusedElement = getFocusedElement(driver)
        return focusedElement != null && focusedElement == element
    }

    override fun actualValue(driver: Driver, element: WebElement): String {
        val focusedElement = getFocusedElement(driver)
        return if (focusedElement == null) "No focused element found " else "Focused element: " + describe.fully(
            driver,
            focusedElement
        ) +
                ", current element: " + describe.fully(driver, element)
    }
}

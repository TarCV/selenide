package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class IsImageLoaded : Condition("is image") {
    override suspend fun apply(driver: Driver, element: WebElement): Boolean {
        return isImage(driver, element)
    }

    companion object {
        fun isImage(driver: Driver, webElement: WebElement): Boolean {
            return driver.executeJavaScript(
                "return arguments[0].tagName.toLowerCase() === 'img' && " +
                        "arguments[0].complete && " +
                        "typeof arguments[0].naturalWidth != 'undefined' && " +
                        "arguments[0].naturalWidth > 0", webElement
            )
        }
    }
}
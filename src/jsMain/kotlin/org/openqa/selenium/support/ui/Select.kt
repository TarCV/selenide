package org.openqa.selenium.support.ui

import org.openqa.selenium.WebElement

actual class Select actual constructor(webElement: org.openqa.selenium.WebElement) {
    actual fun selectByVisibleText(text: String): Unit = TODO("Not yet implemented")
    actual fun selectByIndex(index: Int): Unit = TODO("Not yet implemented")
    actual fun selectByValue(value: String): Unit = TODO("Not yet implemented")

    actual val isMultiple: Boolean
        get() = TODO()
    actual val firstSelectedOption: org.openqa.selenium.WebElement
        get() = TODO()
    actual val allSelectedOptions: List<org.openqa.selenium.WebElement>
        get() = TODO()
}

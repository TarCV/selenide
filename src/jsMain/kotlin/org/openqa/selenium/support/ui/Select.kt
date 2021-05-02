package org.openqa.selenium.support.ui

import org.openqa.selenium.WebElement

class Select(webElement: WebElement) {
    fun selectByVisibleText(text: String): Unit = TODO("Not yet implemented")
    fun selectByIndex(index: Int): Unit = TODO("Not yet implemented")
    fun selectByValue(value: String): Unit = TODO("Not yet implemented")

    val isMultiple: Boolean
        get() = TODO()
    val firstSelectedOption: WebElement
        get() = TODO()
    val allSelectedOptions: List<WebElement>
        get() = TODO()
}

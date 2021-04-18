package org.openqa.selenium

interface SearchContext {
    fun findElement(by: By): WebElement
    fun findElements(by: By): List<WebElement>
}

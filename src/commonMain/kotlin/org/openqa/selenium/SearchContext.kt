package org.openqa.selenium

expect interface SearchContext {
    suspend fun findElement(by: By): WebElement
    suspend fun findElements(by: By): List<WebElement>
}

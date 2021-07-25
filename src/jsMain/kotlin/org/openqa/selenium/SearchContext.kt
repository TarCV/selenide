package org.openqa.selenium

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

actual interface SearchContext {
    actual suspend fun findElement(by: By): WebElement
    actual suspend fun findElements(by: By): List<WebElement>
}

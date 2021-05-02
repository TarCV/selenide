package org.openqa.selenium

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

actual interface SearchContext {
    actual fun findElement(by: org.openqa.selenium.By): org.openqa.selenium.WebElement
    actual fun findElements(by: org.openqa.selenium.By): List<org.openqa.selenium.WebElement>
}

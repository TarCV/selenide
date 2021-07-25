package org.openqa.selenium.internal

import org.openqa.selenium.WebElement

expect interface WrapsElement {
    suspend fun getWrappedElement(): WebElement
}

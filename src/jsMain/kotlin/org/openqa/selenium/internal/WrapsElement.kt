package org.openqa.selenium.internal

import org.openqa.selenium.WebElement

actual interface WrapsElement {
    actual suspend fun getWrappedElement(): WebElement
}

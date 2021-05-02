package org.openqa.selenium.internal

import org.openqa.selenium.WebElement

actual interface WrapsElement {
    actual val wrappedElement: org.openqa.selenium.WebElement
}

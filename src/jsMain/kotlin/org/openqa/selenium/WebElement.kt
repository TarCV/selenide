package org.openqa.selenium

actual interface WebElement: SearchContext {
    actual suspend fun getAttribute(name: String): String?
    actual suspend fun getCssValue(propertyName: String): String
    actual suspend fun getLocation(): Point
    actual suspend fun getRect(): Rectangle
    actual suspend fun getSize(): Dimension
    actual suspend fun getText(): String
    actual suspend fun getTagName(): String
    actual suspend fun isDisplayed(): Boolean
    actual suspend fun isEnabled(): Boolean
    actual suspend fun isSelected(): Boolean

    actual suspend fun clear()
    actual suspend fun click()
    actual suspend fun sendKeys(vararg keys: CharSequence)
    actual suspend fun sendKeys(key: org.openqa.selenium.Keys)
    actual override suspend fun findElement(by: By): WebElement
    actual override suspend fun findElements(by: By): List<WebElement>
    actual suspend fun submit()
}

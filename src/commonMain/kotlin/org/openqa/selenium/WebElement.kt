package org.openqa.selenium

expect interface WebElement: SearchContext {
    suspend fun isDisplayed(): Boolean
    suspend fun isEnabled(): Boolean
    suspend fun isSelected(): Boolean
    suspend fun getAttribute(name: String): String?
    suspend fun getCssValue(propertyName: String): String
    suspend fun getLocation(): Point
    suspend fun getRect(): Rectangle
    suspend fun getSize(): Dimension
    suspend fun getText(): String
    suspend fun getTagName(): String

    suspend fun clear()
    suspend fun click()
    override suspend fun findElement(by: By): WebElement
    override suspend fun findElements(by: By): List<WebElement>
    suspend fun sendKeys(vararg keys: CharSequence)
    suspend fun sendKeys(key: org.openqa.selenium.Keys)
    suspend fun submit()
}

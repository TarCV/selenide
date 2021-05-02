package org.openqa.selenium

interface WebElement: SearchContext {
    val isDisplayed: Boolean
    val isEnabled: Boolean
    val isSelected: Boolean
    val text: String
    val tagName: String

    suspend fun clear()
    suspend fun click()
    suspend fun getAttribute(name: String): String?
    suspend fun getCssValue(propertyName: String): String
    suspend fun getLocation(): Location
    suspend fun sendKeys(keys: String)
    suspend fun sendKeys(key: Keys)
}

package org.openqa.selenium

interface WebDriver: SearchContext {
    val currentUrl: String
    val pageSource: String
    val title: String
    val windowHandle: String
    val windowHandles: Set<String>

    fun manage(): Manager
    fun navigate(): Navigator
    fun switchTo(): TargetLocator

    interface Manager {
        suspend fun deleteAllCookies()
    }
    interface Navigator {
        suspend fun to(url: String)
    }
    interface TargetLocator {
        suspend fun frame(index: Int): WebDriver
        suspend fun frame(nameOrId: String): WebDriver
        suspend fun frame(frameElement: WebElement): WebDriver
        suspend fun parentFrame(): WebDriver
        suspend fun defaultContent(): WebDriver
        suspend fun activeElement(): WebElement
        suspend fun alert(): Alert
        suspend fun window(nameOrHandleOrTitle: String): WebDriver
    }
}

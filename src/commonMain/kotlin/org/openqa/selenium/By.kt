package org.openqa.selenium

expect abstract class By() {
    open class ByXPath(selector: String) : By {
        override suspend fun findElement(context: SearchContext): WebElement
        override suspend fun findElements(context: SearchContext): List<WebElement>
    }
    open class ByCssSelector(selector: String) : By {
        override suspend fun findElement(context: SearchContext): WebElement
        override suspend fun findElements(context: SearchContext): List<WebElement>
    }

    companion object {
        fun cssSelector(selector: String): By
        fun xpath(selector: String): By
        fun name(selector: String): By
        fun linkText(selector: String): By
        fun partialLinkText(selector: String): By
        fun id(selector: String): By
        fun className(selector: String): By
        fun tagName(selector: String): By
    }

    actual abstract suspend fun findElement(context: SearchContext): WebElement
    actual abstract suspend fun findElements(context: SearchContext): List<WebElement>
}

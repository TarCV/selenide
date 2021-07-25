package org.openqa.selenium

actual abstract class By {
    actual open class ByXPath actual constructor(selector: String) : By() {
        actual override suspend fun findElement(context: SearchContext): WebElement {
            TODO("Not yet implemented")
        }

        actual override suspend fun findElements(context: SearchContext): List<WebElement> {
            TODO("Not yet implemented")
        }
    }

    actual open class ByCssSelector actual constructor(selector: String) : By() {
        actual override suspend fun findElement(context: SearchContext): WebElement {
            TODO("Not yet implemented")
        }

        actual override suspend fun findElements(context: SearchContext): List<WebElement> {
            TODO("Not yet implemented")
        }
    }

    actual companion object {
        actual fun cssSelector(selector: String): By = TODO()
        actual fun xpath(selector: String): By = TODO()
        actual fun name(selector: String): By = TODO()
        actual fun linkText(selector: String): By = TODO()
        actual fun partialLinkText(selector: String): By = TODO()
        actual fun id(selector: String): By = TODO()
        actual fun className(selector: String): By = TODO()
        actual fun tagName(selector: String): By = TODO()
    }

    actual abstract suspend fun findElement(context: SearchContext): WebElement

    actual abstract suspend fun findElements(context: SearchContext): List<WebElement>
}

package org.openqa.selenium

interface By {
    companion object {
        fun cssSelector(selector: String): By = TODO()
        fun className(selector: String): By = TODO()
        fun name(selector: String): By = TODO()
        fun xpath(selector: String): By = TODO()
        fun linkText(selector: String): By = TODO()
        fun partialLinkText(selector: String): By = TODO()
        fun id(selector: String): By = TODO()
        fun tagName(selector: String): By = TODO()
    }

    fun findElement(context: SearchContext): WebElement
    fun findElements(context: SearchContext): List<WebElement>

    open class ByXPath(selector: String): By {
        override fun findElement(context: SearchContext): WebElement {
            TODO("Not yet implemented")
        }

        override fun findElements(context: SearchContext): List<WebElement> {
            TODO("Not yet implemented")
        }
    }

    open class ByCssSelector(selector: String): By {
        override fun findElement(context: SearchContext): WebElement {
            TODO("Not yet implemented")
        }

        override fun findElements(context: SearchContext): List<WebElement> {
            TODO("Not yet implemented")
        }
    }
}

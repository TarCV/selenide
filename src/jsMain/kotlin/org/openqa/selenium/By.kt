package org.openqa.selenium

import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement

actual interface By {
    actual companion object {
        actual fun cssSelector(selector: String): org.openqa.selenium.By = TODO()
        actual fun className(selector: String): org.openqa.selenium.By = TODO()
        actual fun name(selector: String): org.openqa.selenium.By = TODO()
        actual fun xpath(selector: String): org.openqa.selenium.By = TODO()
        actual fun linkText(selector: String): org.openqa.selenium.By = TODO()
        actual fun partialLinkText(selector: String): org.openqa.selenium.By = TODO()
        actual fun id(selector: String): org.openqa.selenium.By = TODO()
        actual fun tagName(selector: String): org.openqa.selenium.By = TODO()
    }

    actual fun findElement(context: org.openqa.selenium.SearchContext): org.openqa.selenium.WebElement
    actual fun findElements(context: org.openqa.selenium.SearchContext): List<org.openqa.selenium.WebElement>

    open actual class ByXPath actual constructor(selector: String): org.openqa.selenium.By {
        override actual fun findElement(context: org.openqa.selenium.SearchContext): org.openqa.selenium.WebElement {
            TODO("Not yet implemented")
        }

        override actual fun findElements(context: org.openqa.selenium.SearchContext): List<org.openqa.selenium.WebElement> {
            TODO("Not yet implemented")
        }
    }

    open actual class ByCssSelector actual constructor(selector: String): org.openqa.selenium.By {
        override actual fun findElement(context: org.openqa.selenium.SearchContext): org.openqa.selenium.WebElement {
            TODO("Not yet implemented")
        }

        override actual fun findElements(context: org.openqa.selenium.SearchContext): List<org.openqa.selenium.WebElement> {
            TODO("Not yet implemented")
        }
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelectorMode
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.filecontent.sizzleJs
import org.openqa.selenium.By
import org.openqa.selenium.By.ByCssSelector
import org.openqa.selenium.By.ByXPath
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement

/**
 * Thanks to http://selenium.polteq.com/en/injecting-the-sizzle-css-selector-library/
 */
open class WebElementSelector {
    fun findElement(driver: Driver, context: SearchContext, selector: By): WebElement {
        checkThatXPathNotStartingFromSlash(context, selector)
        if (driver.config().selectorMode() === SelectorMode.CSS || selector !is ByCssSelector) {
            return findElement(context, selector)
        }
        val webElements = evaluateSizzleSelector(driver, context, selector)
        if (webElements.isEmpty()) {
            throw NoSuchElementException("Cannot locate an element using $selector")
        }
        return webElements[0]
    }
    fun findElements(driver: Driver, context: SearchContext, selector: By): List<WebElement> {
        checkThatXPathNotStartingFromSlash(context, selector)
        return if (driver.config().selectorMode() === SelectorMode.CSS || selector !is ByCssSelector) {
            findElements(context, selector)
        } else evaluateSizzleSelector(
            driver,
            context,
            selector
        )
    }

    private fun findElement(context: SearchContext, selector: By): WebElement {
        return if (context is SelenideElement) context.toWebElement().findElement(selector) else context.findElement(
            selector
        )
    }

    private fun findElements(context: SearchContext, selector: By): List<WebElement> {
        return if (context is SelenideElement) context.toWebElement().findElements(selector) else context.findElements(
            selector
        )
    }

    protected fun checkThatXPathNotStartingFromSlash(context: SearchContext?, selector: By) {
        if (context is WebElement) {
            if (selector is ByXPath) {
                require(!selector.toString().startsWith("By.xpath: /")) { "XPath starting from / searches from root" }
            }
        }
    }
    protected fun evaluateSizzleSelector(
        driver: Driver,
        context: SearchContext?,
        sizzleCssSelector: ByCssSelector
    ): List<WebElement> {
        injectSizzleIfNeeded(driver)
        val sizzleSelector = sizzleCssSelector.toString()
            .replace("By.selector: ", "")
            .replace("By.cssSelector: ", "")
        return if (context is WebElement) driver.executeJavaScript(
            "return Sizzle(arguments[0], arguments[1])",
            sizzleSelector,
            context
        ) else driver.executeJavaScript("return Sizzle(arguments[0])", sizzleSelector)
    }

    protected fun injectSizzleIfNeeded(driver: Driver) {
        if (!sizzleLoaded(driver)) {
            injectSizzle(driver)
        }
    }

    protected fun sizzleLoaded(driver: Driver): Boolean {
        return try {
            driver.executeJavaScript("return typeof Sizzle != 'undefined'")
        } catch (e: WebDriverException) {
            false
        }
    }

    protected fun injectSizzle(driver: Driver) = synchronized(this) {
        driver.executeJavaScript<Any>(sizzleJs)
    }

    companion object {
        var instance = WebElementSelector()
    }
}

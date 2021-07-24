package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.cssValue
import com.codeborne.selenide.Condition.Companion.have
import com.codeborne.selenide.Condition.Companion.not
import com.codeborne.selenide.Condition.Companion.or
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.ElementShould.Companion.ElementShould
import com.codeborne.selenide.ex.ElementShouldNot.Companion.ElementShouldNot
import com.codeborne.selenide.impl.ElementFinder.Companion.wrap
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement

abstract class WebElementSource {
    var alias = Alias.NONE
        private set
    abstract fun driver(): Driver
    abstract suspend fun getWebElement(): org.openqa.selenium.WebElement
    abstract fun getSearchCriteria(): String
    fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }
    fun description(): String {
        return alias.getOrElse { getSearchCriteria() }
    }

    override fun toString(): String = description()

    open fun find(proxy: SelenideElement, arg: Any, index: Int): SelenideElement {
        return wrap(driver(), proxy, getSelector(arg), index)
    }
    open suspend fun findAll(): List<org.openqa.selenium.WebElement> {
        return listOf(getWebElement())
    }
    open suspend fun createElementNotFoundError(condition: Condition, lastError: Throwable?): ElementNotFound {
        return ElementNotFound(driver(), description(), condition, lastError)
    }

    suspend fun checkCondition(prefix: String, condition: Condition, invert: Boolean): org.openqa.selenium.WebElement? {
        val check = if (invert) not(condition) else condition
        var lastError: Throwable? = null
        var element: org.openqa.selenium.WebElement? = null
        try {
            element = getWebElement()
            if (check.apply(driver(), element)) {
                return element
            }
        } catch (e: org.openqa.selenium.WebDriverException) {
            lastError = e
        } catch (e: IndexOutOfBoundsException) {
            lastError = e
        } catch (e: AssertionError) {
            lastError = e
        }
        if (lastError != null && Cleanup.of.isInvalidSelectorError(lastError)) {
            throw Cleanup.of.wrap(lastError)
        }
        if (element == null) {
            if (!check.applyNull()) {
                throw createElementNotFoundError(check, lastError)
            }
        } else if (invert) {
            throw ElementShouldNot(driver(), description(), prefix, condition, element, lastError)
        } else {
            throw ElementShould(driver(), description(), prefix, condition, element, lastError)
        }
        return null
    }

    /**
     * Asserts that returned element can be interacted with.
     *
     * Elements which are transparent (opacity:0) are considered to be invisible, but interactable.
     * User (as of 05.12.2018) can click, doubleClick etc., and enter text etc. to transparent elements
     * for all major browsers
     *
     * @return element or throws ElementShould/ElementShouldNot exceptions
     */
    suspend fun findAndAssertElementIsInteractable(): org.openqa.selenium.WebElement {
        return checkNotNull(
          checkCondition(
            "be ",
            or("visible or transparent", Condition.visible, have(cssValue("opacity", "0"))),
            false
          )
        )
    }

    companion object {
        fun getSelector(arg: Any): org.openqa.selenium.By {
            return if (arg is org.openqa.selenium.By) arg else org.openqa.selenium.By.cssSelector(arg as String)
        }
    }
}

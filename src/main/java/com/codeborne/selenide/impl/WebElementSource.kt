package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.cssValue
import com.codeborne.selenide.Condition.Companion.have
import com.codeborne.selenide.Condition.Companion.not
import com.codeborne.selenide.Condition.Companion.or
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.ElementShould
import com.codeborne.selenide.ex.ElementShouldNot
import com.codeborne.selenide.impl.ElementFinder.Companion.wrap
import org.openqa.selenium.By
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import java.util.Objects
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
abstract class WebElementSource {
    @get:CheckReturnValue
    var alias = Alias.NONE
        private set

    @CheckReturnValue
    abstract fun driver(): Driver

    @get:CheckReturnValue
    abstract val webElement: WebElement

    @get:CheckReturnValue
    abstract val searchCriteria: String
    fun setAlias(alias: String) {
        this.alias = Alias(alias)
    }

    @CheckReturnValue
    fun description(): String {
        return alias.getOrElse { searchCriteria }
    }

    @CheckReturnValue
    override fun toString(): String {
        return description()
    }

    @CheckReturnValue
    open fun find(proxy: SelenideElement, arg: Any, index: Int): SelenideElement {
        return wrap(driver(), proxy, getSelector(arg), index)
    }

    @CheckReturnValue
    @Throws(IndexOutOfBoundsException::class)
    open fun findAll(): List<WebElement> {
        return listOf(webElement)
    }

    @CheckReturnValue
    open fun createElementNotFoundError(condition: Condition, lastError: Throwable?): ElementNotFound {
        return ElementNotFound(driver(), description(), condition, lastError)
    }

    fun checkCondition(prefix: String, condition: Condition?, invert: Boolean): WebElement? {
        val check = if (invert) not(condition!!) else condition!!
        var lastError: Throwable? = null
        var element: WebElement? = null
        try {
            element = webElement
            if (check.apply(driver(), element)) {
                return element
            }
        } catch (e: WebDriverException) {
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
    @CheckReturnValue
    fun findAndAssertElementIsInteractable(): WebElement {
        return Objects.requireNonNull(
            checkCondition(
                "be ",
                or("visible or transparent", Condition.visible, have(cssValue("opacity", "0"))),
                false
            )
        )!!
    }

    companion object {
        @CheckReturnValue
        fun getSelector(arg: Any?): By {
            return if (arg is By) arg else By.cssSelector(arg as String?)
        }
    }
}

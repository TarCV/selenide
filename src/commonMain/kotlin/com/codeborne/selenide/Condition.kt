package com.codeborne.selenide

import com.codeborne.selenide.conditions.And
import com.codeborne.selenide.conditions.Attribute
import com.codeborne.selenide.conditions.AttributeWithValue
import com.codeborne.selenide.conditions.CaseSensitiveText
import com.codeborne.selenide.conditions.Checked
import com.codeborne.selenide.conditions.ConditionHelpers
import com.codeborne.selenide.conditions.CssClass
import com.codeborne.selenide.conditions.CssValue
import com.codeborne.selenide.conditions.CustomMatch
import com.codeborne.selenide.conditions.Disabled
import com.codeborne.selenide.conditions.Enabled
import com.codeborne.selenide.conditions.ExactOwnText
import com.codeborne.selenide.conditions.ExactText
import com.codeborne.selenide.conditions.ExactTextCaseSensitive
import com.codeborne.selenide.conditions.Exist
import com.codeborne.selenide.conditions.ExplainedCondition
import com.codeborne.selenide.conditions.Focused
import com.codeborne.selenide.conditions.Hidden
import com.codeborne.selenide.conditions.IsImageLoaded
import com.codeborne.selenide.conditions.MatchAttributeWithValue
import com.codeborne.selenide.conditions.MatchText
import com.codeborne.selenide.conditions.NamedCondition
import com.codeborne.selenide.conditions.Not
import com.codeborne.selenide.conditions.Or
import com.codeborne.selenide.conditions.OwnText
import com.codeborne.selenide.conditions.PseudoElementPropertyWithValue
import com.codeborne.selenide.conditions.Selected
import com.codeborne.selenide.conditions.SelectedText
import com.codeborne.selenide.conditions.Text
import com.codeborne.selenide.conditions.Value
import com.codeborne.selenide.conditions.Visible
import org.openqa.selenium.WebElement
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlinx.coroutines.runBlocking

/**
 * Conditions to match web elements: checks for visibility, text etc.
 */
abstract class Condition constructor(
    val name: String,
    private val absentElementMatchesCondition: Boolean = false
) {
    open fun negate(): Condition {
        return Not(this, absentElementMatchesCondition)
    }

    /**
     * Check if given element matches this condition.
     *
     * @param element given WebElement
     * @return true if element matches condition
     */
    abstract suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean

    fun applyBlocking(driver: Driver, element: org.openqa.selenium.WebElement): Boolean = runBlocking {
        apply(driver, element)
    }

    fun applyNull(): Boolean {
        return absentElementMatchesCondition
    }

    /**
     * If element didn't match the condition, returns the actual value of element.
     * Used in error reporting.
     * Optional. Makes sense only if you need to add some additional important info to error message.
     *
     * @param driver given driver
     * @param element given WebElement
     * @return any string that needs to be appended to error message.
     */
    open suspend fun actualValue(driver: Driver, element: org.openqa.selenium.WebElement): String? {
        return null
    }

    /**
     * Should be used for explaining the reason of condition
     */
    fun because(message: String): Condition {
        return ExplainedCondition(this, message)
    }

    override fun toString(): String {
        return name
    }

    fun missingElementSatisfiesCondition(): Boolean {
        return absentElementMatchesCondition
    }

    companion object {
        /**
         * Checks if element is visible
         *
         *
         * Sample: `$("input").shouldBe(visible);`
         */
        @JvmField
        val visible: Condition = Visible()

        /**
         * Check if element exist. It can be visible or hidden.
         *
         *
         * Sample: `$("input").should(exist);`
         */
        @JvmField
        val exist: Condition = Exist()

        /**
         * Checks that element is not visible or does not exists.
         *
         *
         * Opposite to [.appear]
         *
         *
         * Sample: `$("input").shouldBe(hidden);`
         */
        @JvmField
        val hidden: Condition = Hidden()

        /**
         * Synonym for [.visible] - may be used for better readability
         *
         *
         * Sample: `$("#logoutLink").should(appear);`
         */
        @JvmField
        val appear = visible

        /**
         * Synonym for [.visible] - may be used for better readability
         *
         * `$("#logoutLink").waitUntil(appears, 10000);`
         */
        @JvmField
        @Deprecated("use {@link #visible} or {@link #appear}")
        val appears = visible

        /**
         * Synonym for [.hidden] - may be used for better readability:
         *
         *
         * Sample: `$("#loginLink").waitUntil(disappears, 9000);`
         *
         */
        @JvmField
        @Deprecated("use {@link #disappear} or {@link #hidden}")
        val disappears = hidden

        /**
         * Synonym for [.hidden] - may be used for better readability:
         *
         *
         * `$("#loginLink").should(disappear);`
         */
        @JvmField
        val disappear = hidden

        /**
         * Check if element has "readonly" attribute (with any value)
         *
         *
         * Sample: `$("input").shouldBe(readonly);`
         */
        @JvmField
        val readonly = attribute("readonly")

        /**
         * Check if element has given attribute (with any value)
         *
         *
         * Sample: `$("#mydiv").shouldHave(attribute("fileId"));`
         *
         * @param attributeName name of attribute, not null
         * @return true iff attribute exists
         */
        @JvmStatic
        fun attribute(attributeName: String): Condition {
            return Attribute(attributeName)
        }

        /**
         *
         * Sample: `$("#mydiv").shouldHave(attribute("fileId", "12345"));`
         *
         * @param attributeName          name of attribute
         * @param expectedAttributeValue expected value of attribute
         */
        @JvmStatic
        fun attribute(attributeName: String, expectedAttributeValue: String): Condition {
            return AttributeWithValue(attributeName, expectedAttributeValue)
        }

        /**
         * Assert that given element's attribute matches given regular expression
         *
         *
         * Sample: `$("h1").should(attributeMatching("fileId", ".*12345.*"))`
         *
         * @param attributeName  name of attribute
         * @param attributeRegex regex to match attribute value
         */
        @JvmStatic
        fun attributeMatching(attributeName: String, attributeRegex: String): Condition {
            return MatchAttributeWithValue(attributeName, attributeRegex)
        }

        /**
         *
         * Sample: `$("#mydiv").shouldHave(href("/one/two/three.pdf"));`
         *
         * It looks similar to `$.shouldHave(attribute("href", href))`, but
         * it overcomes the fact that Selenium returns full url (even if "href" attribute in html contains relative url).
         *
         * @param href expected value of "href" attribute
         */
        /* TODO: fun href(href: String): Condition {
            return Href(href)
        }*/

        /**
         * Assert that element contains given "value" attribute as substring
         * NB! Ignores difference in non-visible characters like spaces, non-breakable spaces, tabs, newlines  etc.
         *
         *
         * Sample: `$("input").shouldHave(value("12345 666 77"));`
         *
         * @param expectedValue expected value of "value" attribute
         */
        @JvmStatic
        fun value(expectedValue: String): Condition {
            return Value(expectedValue)
        }

        /**
         * Check that element has given the property value of the pseudo-element
         *
         * Sample: `$("input").shouldHave(pseudo(":first-letter", "color", "#ff0000"));`
         *
         * @param pseudoElementName pseudo-element name of the element,
         * ":before", ":after", ":first-letter", ":first-line", ":selection"
         * @param propertyName property name of the pseudo-element
         * @param expectedValue expected value of the property
         */
        @JvmStatic
        fun pseudo(pseudoElementName: String, propertyName: String, expectedValue: String): Condition {
            return PseudoElementPropertyWithValue(pseudoElementName, propertyName, expectedValue)
        }

        /**
         * Check that element has given the "content" property of the pseudo-element
         *
         * Sample: `$("input").shouldHave(pseudo(":before", "Hello"));`
         *
         * @param pseudoElementName pseudo-element name of the element, ":before", ":after"
         * @param expectedValue expected content of the pseudo-element
         */
        @JvmStatic
        fun pseudo(pseudoElementName: String, expectedValue: String): Condition {
            return PseudoElementPropertyWithValue(pseudoElementName, "content", expectedValue)
        }

        /**
         *
         * Sample: `$("#input").shouldHave(exactValue("John"));`
         *
         * @param value expected value of input field
         */
        @JvmStatic
        fun exactValue(value: String): Condition {
            return attribute("value", value)
        }

        /**
         * Asserts the name attribute of the element to be exact string
         *
         * Sample: `$("#input").shouldHave(name("username"))`
         *
         * @param name expected name of input field
         */
        @JvmStatic
        fun name(name: String): Condition {
            return attribute("name", name)
        }

        /**
         * Asserts the type attribute of the element to be exact string
         *
         * Sample: `$("#input").shouldHave(type("checkbox"))`
         *
         * @param type expected type of input field
         */
        @JvmStatic
        fun type(type: String): Condition {
            return attribute("type", type)
        }

        /**
         *
         * Sample: `$("#input").shouldHave(id("myForm"))`
         *
         * @param id expected id of input field
         */
        @JvmStatic
        fun id(id: String): Condition {
            return attribute("id", id)
        }

        /**
         * 1) For input element, check that value is missing or empty
         *
         * Sample: `$("#input").shouldBe(empty)`
         *
         *
         * 2) For other elements, check that text is empty
         *
         * Sample: `$("h2").shouldBe(empty)`
         */
        @JvmField
        val empty = and("empty", exactValue(""), exactText(""))

        /**
         * The same as matchText()
         *
         * Sample: `$(".error_message").waitWhile(matchesText("Exception"), 12000)`
         *
         * @see .matchText
         */
        @Deprecated("use {@link #matchText(String)}")
        @JvmStatic
        fun matchesText(text: String): Condition {
                return matchText(text)
        }

        /**
         * Assert that given element's text matches given regular expression
         *
         *
         * Sample: `$("h1").should(matchText("Hello\s*John"))`
         *
         * @param regex e.g. Kicked.*Chuck Norris - in this case ".*" can contain any characters including spaces, tabs, CR etc.
         */
        @JvmStatic
        fun matchText(regex: String): Condition {
            return MatchText(regex)
        }

        /**
         *
         *
         * Assert that element contains given text as a substring.
         *
         *
         *
         * Sample: `$("h1").shouldHave(text("Hello\s*John"))`
         *
         *
         * NB! Case insensitive
         *
         * NB! Ignores multiple whitespaces between words
         *
         * @param text expected text of HTML element.
         * NB! Empty string is not allowed (because any element does contain an empty text).
         *
         * @throws IllegalArgumentException if given text is null or empty
         */
        @JvmStatic
        fun text(text: String): Condition {
            return Text(text)
        }

        /**
         * Checks on a element that exactly given text is selected (=marked with mouse/keyboard)
         *
         *
         * Sample: `$("input").shouldHave(selectedText("Text"))`
         *
         *
         * NB! Case sensitive
         *
         * @param expectedText expected selected text of the element
         */
        @JvmStatic
        fun selectedText(expectedText: String): Condition {
            return SelectedText(expectedText)
        }

        /**
         * Assert that element contains given text as a case sensitive substring
         *
         *
         * Sample: `$("h1").shouldHave(textCaseSensitive("Hello\s*John"))`
         *
         *
         * NB! Ignores multiple whitespaces between words
         *
         * @param text expected text of HTML element
         */
        @JvmStatic
        fun textCaseSensitive(text: String): Condition {
            return CaseSensitiveText(text)
        }

        /**
         * Assert that element has exactly (case insensitive) given text
         *
         * Sample: `$("h1").shouldHave(exactText("Hello"))`
         *
         *
         * Case insensitive
         *
         * NB! Ignores multiple whitespaces between words
         *
         * @param text expected text of HTML element
         */
        @JvmStatic
        fun exactText(text: String): Condition {
            return ExactText(text)
        }

        /**
         * Assert that element contains given text (without checking child elements).
         *
         * Sample: `$("h1").shouldHave(ownText("Hello"))`
         *
         *
         * Case insensitive
         *
         * NB! Ignores multiple whitespaces between words
         *
         * @param text expected text of HTML element without its children
         */
        @JvmStatic
        fun ownText(text: String): Condition {
            return OwnText(text)
        }

        /**
         * Assert that element has given text (without checking child elements).
         *
         * Sample: `$("h1").shouldHave(ownText("Hello"))`
         *
         *
         * Case insensitive
         *
         * NB! Ignores multiple whitespaces between words
         *
         * @param text expected text of HTML element without its children
         */
        @JvmStatic
        fun exactOwnText(text: String): Condition {
            return ExactOwnText(text)
        }

        /**
         * Assert that element has exactly the given text
         *
         * Sample: `$("h1").shouldHave(exactTextCaseSensitive("Hello"))`
         *
         *
         * NB! Ignores multiple whitespaces between words
         *
         * @param text expected text of HTML element
         */
        @JvmStatic
        fun exactTextCaseSensitive(text: String): Condition {
            return ExactTextCaseSensitive(text)
        }

        /**
         * Asserts that element has the given class. Element may other classes too.
         *
         * Sample: `$("input").shouldHave(cssClass("active"));`
         */
        @JvmStatic
        fun cssClass(cssClass: String): Condition {
            return CssClass(cssClass)
        }

        /**
         * Checks if css property (style) applies for the element.
         * Both explicit and computed properties are supported.
         *
         *
         * Note that if css property is missing [WebElement.getCssValue] return empty string.
         * In this case you should assert against empty string.
         *
         *
         * Sample:
         *
         *
         * `<input style="font-size: 12">`
         *
         *
         * `$("input").shouldHave(cssValue("font-size", "12"));`
         *
         *
         * `$("input").shouldHave(cssValue("display", "block"));`
         *
         * @param propertyName  the css property (style) name  of the element
         * @param expectedValue expected value of css property
         * @see WebElement.getCssValue
         */
        @JvmStatic
        fun cssValue(propertyName: String, expectedValue: String): Condition {
            return CssValue(propertyName, expectedValue)
        }

        /**
         * Checks if element matches the given predicate.
         *
         *
         * Sample: `$("input").should(match("empty value attribute", el -> el.getAttribute("value").isEmpty()));`
         *
         * @param description the description of the predicate
         * @param predicate   the [Predicate] to match
         */
        @JvmStatic
        fun match(description: String, predicate: (org.openqa.selenium.WebElement) -> Boolean): Condition {
            return CustomMatch(description, predicate)
        }

        /**
         * Check if image is loaded.
         */
        @JvmField
        val image: Condition = IsImageLoaded()

        /**
         * Check if browser focus is currently in given element.
         */
        @JvmField
        val focused: Condition = Focused()

        /**
         * Checks that element is not disabled
         *
         * @see WebElement.isEnabled
         */
        @JvmField
        val enabled: Condition = Enabled()

        /**
         * Checks that element is disabled
         *
         * @see WebElement.isEnabled
         */
        @JvmField
        val disabled: Condition = Disabled()

        /**
         * Checks that element is selected (inputs like drop-downs etc.)
         *
         * @see WebElement.isSelected
         */
        @JvmField
        val selected: Condition = Selected()

        /**
         * Checks that checkbox is checked
         *
         * @see WebElement.isSelected
         */
        @JvmField
        val checked: Condition = Checked()

        /**
         * Negate given condition.
         *
         *
         * Used for methods like $.shouldNot(exist), $.shouldNotBe(visible)
         *
         *
         * Typically you don't need to use it.
         */
        @JvmStatic
        fun not(condition: Condition): Condition {
            return condition.negate()
        }

        /**
         * Check if element matches ALL given conditions.
         * The method signature makes you to pass at least 2 conditions, otherwise it would be nonsense.
         *
         * @param name       Name of this condition, like "empty" (meaning e.g. empty text AND empty value).
         * @param condition1 first condition to match
         * @param condition2 second condition to match
         * @param conditions Other conditions to match
         * @return logical AND for given conditions.
         */
        @JvmStatic
        fun and(name: String, condition1: Condition, condition2: Condition, vararg conditions: Condition): Condition {
            return And(name, ConditionHelpers.merge(condition1, condition2, *conditions))
        }

        /**
         * Check if element matches ANY of given conditions.
         * The method signature makes you to pass at least 2 conditions, otherwise it would be nonsense.
         *
         * @param name       Name of this condition, like "error" (meaning e.g. "error" OR "failed").
         * @param condition1 first condition to match
         * @param condition2 second condition to match
         * @param conditions Other conditions to match
         * @return logical OR for given conditions.
         */
        @JvmStatic
        fun or(name: String, condition1: Condition, condition2: Condition, vararg conditions: Condition): Condition {
            return Or(name, ConditionHelpers.merge(condition1, condition2, *conditions))
        }

        /**
         * Used to form human-readable condition expression
         * Example element.should(be(visible),have(text("abc"))
         *
         * @param delegate next condition to wrap
         * @return Condition
         */
        @JvmStatic
        fun be(delegate: Condition): Condition {
            return wrap("be", delegate)
        }

        /**
         * Used to form human-readable condition expression
         * Example element.should(be(visible),have(text("abc"))
         *
         * @param delegate next condition to wrap
         * @return Condition
         */
        @JvmStatic
        fun have(delegate: Condition): Condition {
            return wrap("have", delegate)
        }

        private fun wrap(prefix: String, delegate: Condition): Condition {
            return NamedCondition(prefix, delegate)
        }
    }
}

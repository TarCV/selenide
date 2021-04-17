package com.codeborne.selenide

import com.codeborne.selenide.selector.ByShadow
import org.openqa.selenium.By
import org.openqa.selenium.By.ByXPath
import org.openqa.selenium.support.ui.Quotes
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
object Selectors {
    private const val NORMALIZE_SPACE_XPATH = "normalize-space(translate(string(.), '\t\n\r\u00a0', '    '))"

    /**
     * Find element CONTAINING given text (as a substring).
     *
     *
     * This method ignores difference between space, \n, \r, \t and &nbsp;
     * This method ignores multiple spaces.
     *
     * @param elementText Text to search inside element
     * @return standard selenium By criteria`
     */
    @JvmStatic
    @CheckReturnValue
    fun withText(elementText: String): By {
        return WithText(elementText)
    }

    /**
     * Find element that has given text (the whole text, not a substring).
     *
     *
     * This method ignores difference between space, \n, \r, \t and &nbsp;
     * This method ignores multiple spaces.
     *
     * @param elementText Text that searched element should have
     * @return standard selenium By criteria
     */
    @JvmStatic
    @CheckReturnValue
    fun byText(elementText: String): By {
        return ByText(elementText)
    }

    /**
     * Find elements having attribute with given value.
     *
     *
     * Examples:
     * `<div binding="fieldValue"></div>`
     * Find element with attribute 'binding' EXACTLY containing text 'fieldValue' , use:
     * byAttribute("binding", "fieldValue")
     *
     *
     * For finding difficult/generated data attribute which contains some value:
     * `<div binding="userName17fk5n6kc2Ds45F40d0fieldValue_promoLanding word"></div>`
     *
     *
     * Find element with attribute 'binding' CONTAINING text 'fieldValue', use symbol '*' with attribute name:
     * byAttribute("binding*", "fieldValue") it same as By.cssSelector("[binding*='fieldValue']")
     *
     *
     * Find element whose attribute 'binding' BEGINS with 'userName', use symbol '^' with attribute name:
     * byAttribute("binding^", "fieldValue")
     *
     *
     * Find element whose attribute 'binding' ENDS with 'promoLanding', use symbol '$' with attribute name:
     * byAttribute("binding$", "promoLanding")
     *
     *
     * Find element whose attribute 'binding' CONTAINING WORD 'word':
     * byAttribute("binding~", "word")
     *
     *
     * Seems to work incorrectly if attribute name contains dash, for example: `<option data-mailServerId="123"></option>`
     *
     * @param attributeName  name of attribute, should not be empty or null
     * @param attributeValue value of attribute, should not contain both apostrophes and quotes
     * @return standard selenium By cssSelector criteria
     */
    @JvmStatic
    @CheckReturnValue
    fun byAttribute(attributeName: String, attributeValue: String): By {
        val escapedAttributeValue =
            attributeValue.replace("\\\\".toRegex(), "\\\\\\\\").replace("\"".toRegex(), "\\\\\"")
        return By.cssSelector(String.format("[%s=\"%s\"]", attributeName, escapedAttributeValue))
    }

    /**
     * @see ByShadow.cssSelector
     * @since 5.10
     */
    @JvmStatic
    @CheckReturnValue
    fun shadowCss(target: String, shadowHost: String, vararg innerShadowHosts: String): By {
        return ByShadow.cssSelector(target, shadowHost, *innerShadowHosts)
    }

    /**
     * Synonym for #byAttribute
     */
    @JvmStatic
    @CheckReturnValue
    fun by(attributeName: String, attributeValue: String): By {
        return byAttribute(attributeName, attributeValue)
    }

    /**
     * Find element with given title ("title" attribute)
     */
    @JvmStatic
    @CheckReturnValue
    fun byTitle(title: String): By {
        return byAttribute("title", title)
    }

    /**
     * Find input element with given value ("value" attribute)
     */
    @JvmStatic
    @CheckReturnValue
    fun byValue(value: String): By {
        return byAttribute("value", value)
    }

    /**
     * @see By.name
     * @since 3.1
     */
    @JvmStatic
    @CheckReturnValue
    fun byName(name: String): By {
        return By.name(name)
    }

    /**
     * @see By.xpath
     * @since 3.1
     */
    @JvmStatic
    @CheckReturnValue
    fun byXpath(xpath: String): By {
        return By.xpath(xpath)
    }

    /**
     * @see By.linkText
     * @since 3.1
     */
    @JvmStatic
    @CheckReturnValue
    fun byLinkText(linkText: String): By {
        return By.linkText(linkText)
    }

    /**
     * @see By.partialLinkText
     * @since 3.1
     */
    @JvmStatic
    @CheckReturnValue
    fun byPartialLinkText(partialLinkText: String): By {
        return By.partialLinkText(partialLinkText)
    }

    /**
     * @see By.id
     * @since 3.1
     */
    @JvmStatic
    @CheckReturnValue
    fun byId(id: String): By {
        return By.id(id)
    }

    /**
     * @see By.cssSelector
     * @since 3.8
     */
    @JvmStatic
    @CheckReturnValue
    fun byCssSelector(css: String): By {
        return By.cssSelector(css)
    }

    /**
     * @see By.className
     * @since 3.8
     */
    @JvmStatic
    @CheckReturnValue
    fun byClassName(className: String): By {
        return By.className(className)
    }

    /**
     * @see By.tagName
     * @since 5.11
     */
    @JvmStatic
    @CheckReturnValue
    fun byTagName(tagName: String): By {
        return By.tagName(tagName)
    }

    @ParametersAreNonnullByDefault
    class ByText(protected val elementText: String) : ByXPath(
        ".//*/text()[" + NORMALIZE_SPACE_XPATH + " = " + Quotes.escape(
            elementText
        ) + "]/parent::*"
    ) {
        @CheckReturnValue
            override fun toString(): String {
            return "by text: $elementText"
        }

        @get:CheckReturnValue
        val xPath: String
            get() = super.toString().replace("By.xpath: ", "")
    }

    @ParametersAreNonnullByDefault
    class WithText(protected val elementText: String) : ByXPath(
        ".//*/text()[contains(" + NORMALIZE_SPACE_XPATH + ", " + Quotes.escape(
            elementText
        ) + ")]/parent::*"
    ) {
        @CheckReturnValue
            override fun toString(): String {
            return "with text: $elementText"
        }

        @get:CheckReturnValue
        val xPath: String
            get() = super.toString().replace("By.xpath: ", "")
    }
}

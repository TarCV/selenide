package com.codeborne.selenide

import com.codeborne.selenide.files.FileFilter
import okio.ExperimentalFileSystem
import okio.Path
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebElement
import org.openqa.selenium.WrapsDriver
import org.openqa.selenium.interactions.Locatable
import org.openqa.selenium.internal.HasIdentity
import org.openqa.selenium.internal.WrapsElement
import kotlin.time.Duration

/**
 * Wrapper around [WebElement] with additional methods like
 * [.shouldBe] and [.shouldHave]
 */
interface SelenideElement : WebElement, WrapsDriver, WrapsElement, Locatable, TakesScreenshot, HasIdentity {
    /**
     * **Implementation details:**
     *
     *
     * If Configuration.versatileSetValue is true, can work as 'selectOptionByValue', 'selectRadio'
     *
     *
     * If Configuration.fastSetValue is true, sets value by javascript instead of using Selenium built-in "sendKey" function
     * and trigger "focus", "keydown", "keypress", "input", "keyup", "change" events.
     *
     *
     * In other case behavior will be:
     * <pre>
     * 1. WebElement.clear()
     * 2. WebElement.sendKeys(text)
     * 3. Trigger change event
    </pre> *
     *
     * @param text Any text to enter into the text field or set by value for select/radio.
     * @see com.codeborne.selenide.commands.SetValue
     */
    fun setValue(text: String): SelenideElement

    /**
     * Same as [.setValue]
     *
     * @see com.codeborne.selenide.commands.Val
     */
    fun `val`(text: String): SelenideElement

  /**
     * Append given text to the text field and trigger "change" event.
     *
     *
     * Implementation details:
     * This is the same as
     * <pre>
     * 1. WebElement.sendKeys(text)
     * 2. Trigger change event
    </pre> *
     *
     * @param text Any text to append into the text field.
     * @see com.codeborne.selenide.commands.Append
     */
    fun append(text: String): SelenideElement

  /**
     * Press ENTER. Useful for input field and textareas: <pre>
     * $("query").val("Aikido techniques").pressEnter();</pre>
     *
     *
     * Implementation details:
     * Check that element is displayed and execute <pre>
     * WebElement.sendKeys(Keys.ENTER)</pre>
     *
     * @see com.codeborne.selenide.commands.PressEnter
     */
    fun pressEnter(): SelenideElement

  /**
     * Press TAB. Useful for input field and textareas: <pre>
     * $("#to").val("stiven@seagal.com").pressTab();</pre>
     *
     *
     * Implementation details:
     * Check that element is displayed and execute <pre>
     * WebElement.sendKeys(Keys.TAB)</pre>
     *
     * @see com.codeborne.selenide.commands.PressTab
     */
    fun pressTab(): SelenideElement

  /**
     * Press ESCAPE. Useful for input field and textareas: <pre>
     * $(".edit").click().pressEscape();</pre>
     *
     *
     * Implementation details:
     * Check that element is displayed and execute <pre>
     * WebElement.sendKeys(Keys.ESCAPE)</pre>
     *
     * @see com.codeborne.selenide.commands.PressEscape
     */
    fun pressEscape(): SelenideElement

  /**
     * Get the visible text of this element, including sub-elements without leading/trailing whitespace.
     * NB! For "select", returns text(s) of selected option(s).
     *
     * @return The innerText of this element
     * @see com.codeborne.selenide.commands.GetText
     */
    override val text: String

    /**
     * Element alias, which can be set with [.as]
     *
     * @return Alias of this element or null, if element alias is not set
     * @see com.codeborne.selenide.commands.GetAlias
     *
     * @since 5.20.0
     */
    val alias: String?

  /**
     * Get the text of the element WITHOUT children.
     *
     * @see com.codeborne.selenide.commands.GetOwnText
     */
    val ownText: String

  /**
     * Get the text code of the element with children.
     *
     *
     * It can be used to get the text of a hidden element.
     *
     *
     * Short form of getAttribute("textContent") or getAttribute("innerText") depending on browser.
     *
     *
     * @see com.codeborne.selenide.commands.GetInnerText
     */
    fun innerText(): String

  /**
     * Get the HTML code of the element with children.
     *
     *
     * It can be used to get the html of a hidden element.
     *
     *
     * Short form of getAttribute("innerHTML")
     *
     *
     * @see com.codeborne.selenide.commands.GetInnerHtml
     */
    fun innerHtml(): String

  /**
     * Get the attribute of the element. Synonym for [.getAttribute]
     *
     * @return null if attribute is missing
     * @see com.codeborne.selenide.commands.GetAttribute
     */
    fun attr(attributeName: String): String?

    /**
     * Get the "name" attribute of the element
     *
     * @return attribute "name" value or null if attribute is missing
     * @see com.codeborne.selenide.commands.GetName
     */
    fun name(): String?

    /**
     * Get the "value" attribute of the element
     * Same as [.getValue]
     *
     * @return attribute "value" value or null if attribute is missing
     * @see com.codeborne.selenide.commands.Val
     */
    fun `val`(): String?

    /**
     * Get the "value" attribute of the element
     *
     * @return attribute "value" value or null if attribute is missing
     * @see com.codeborne.selenide.commands.GetValue
     *
     * @since 3.1
     */
    val value: String?

    /**
     * Get the property value of the pseudo-element
     *
     * @param pseudoElementName pseudo-element name of the element,
     * ":before", ":after", ":first-letter", ":first-line", ":selection"
     * @param propertyName      property name of the pseudo-element
     * @return the property value or "" if the property is missing
     * @see com.codeborne.selenide.commands.GetPseudoValue
     */
    fun pseudo(pseudoElementName: String, propertyName: String): String

  /**
     * Get content of the pseudo-element
     *
     * @param pseudoElementName pseudo-element name of the element, ":before", ":after"
     * @return the content value or "none" if the content is missing
     * @see com.codeborne.selenide.commands.GetPseudoValue
     */
    fun pseudo(pseudoElementName: String): String

  /**
     * Select radio button
     *
     * @param value value of radio button to select
     * @return selected "input type=radio" element
     * @see com.codeborne.selenide.commands.SelectRadio
     */
    fun selectRadio(value: String): SelenideElement

  /**
     * Get value of attribute "data-*dataAttributeName*"
     *
     * @see com.codeborne.selenide.commands.GetDataAttribute
     */
    fun data(dataAttributeName: String): String?

    /**
     * {@inheritDoc}
     */
    override suspend fun getAttribute(name: String): String?

    /**
     * {@inheritDoc}
     */
    override suspend fun getCssValue(propertyName: String): String

    /**
     * Checks if element exists true on the current page.
     *
     * @return false if element is not found, browser is closed or any WebDriver exception happened
     * @see com.codeborne.selenide.commands.Exists
     */
    suspend fun exists(): Boolean

    /**
     * Check if this element exists and visible.
     *
     * @return false if element does not exists, is invisible, browser is closed or any WebDriver exception happened.
     */
    override val isDisplayed: Boolean

    /**
     * immediately returns true if element matches given condition
     * Method doesn't wait!
     * WARNING: This method can help implementing crooks, but it is not needed for typical ui tests.
     *
     * @see .has
     *
     * @see com.codeborne.selenide.commands.Matches
     */
    fun `is`(condition: Condition): Boolean

    /**
     * immediately returns true if element matches given condition
     * Method doesn't wait!
     * WARNING: This method can help implementing crooks, but it is not needed for typical ui tests.
     *
     * @see .is
     *
     * @see com.codeborne.selenide.commands.Matches
     */
    fun has(condition: Condition): Boolean

    /**
     * Set checkbox state to CHECKED or UNCHECKED.
     *
     * @param selected true for checked and false for unchecked
     * @see com.codeborne.selenide.commands.SetSelected
     */
    fun setSelected(selected: Boolean): SelenideElement

  /**
     *
     * Checks that given element meets all of given conditions.
     *
     *
     *
     * IMPORTANT: If element does not match then conditions immediately, waits up to
     * 4 seconds until element meets the conditions. It's extremely useful for dynamic content.
     *
     *
     *
     * Timeout is configurable via [com.codeborne.selenide.Configuration.timeout]
     *
     *
     * For example: `$("#errorMessage").should(appear);
    ` *
     *
     * @return Given element, useful for chaining:
     * `$("#errorMessage").should(appear).shouldBe(enabled);`
     * @see com.codeborne.selenide.Config.timeout
     *
     * @see com.codeborne.selenide.commands.Should
     */
    fun should(vararg condition: Condition): SelenideElement

  /**
     * Wait until given element meets given condition (with given timeout)
     */
    @kotlin.time.ExperimentalTime
    fun should(condition: Condition, timeout: Duration): SelenideElement

  /**
     *
     * Synonym for [.should]. Useful for better readability.
     *
     * For example: `$("#errorMessage").shouldHave(text("Hello"), text("World"));
    ` *
     *
     * @see SelenideElement.should
     * @see com.codeborne.selenide.commands.ShouldHave
     */
    fun shouldHave(vararg condition: Condition): SelenideElement

  /**
     * Wait until given element meets given condition (with given timeout)
     */
    @kotlin.time.ExperimentalTime
    fun shouldHave(condition: Condition, timeout: Duration): SelenideElement

  /**
     *
     * Synonym for [.should]. Useful for better readability.
     *
     * For example: `$("#errorMessage").shouldBe(visible, enabled);
    ` *
     *
     * @see SelenideElement.should
     * @see com.codeborne.selenide.commands.ShouldBe
     */
    fun shouldBe(vararg condition: Condition): SelenideElement

    /**
     * Wait until given element meets given condition (with given timeout)
     */
    @kotlin.time.ExperimentalTime
    fun shouldBe(condition: Condition, timeout: Duration): SelenideElement

  /**
     *
     * Checks that given element does not meet given conditions.
     *
     *
     *
     * IMPORTANT: If element does match the conditions, waits up to
     * 4 seconds until element does not meet the conditions. It's extremely useful for dynamic content.
     *
     *
     *
     * Timeout is configurable via [com.codeborne.selenide.Configuration.timeout]
     *
     *
     * For example: `$("#errorMessage").should(exist);
    ` *
     *
     * @see com.codeborne.selenide.Config.timeout
     *
     * @see com.codeborne.selenide.commands.ShouldNot
     */
    fun shouldNot(vararg condition: Condition): SelenideElement

    /**
     * Wait until given element meets given condition (with given timeout)
     */
    @kotlin.time.ExperimentalTime
    fun shouldNot(condition: Condition, timeout: Duration): SelenideElement

  /**
     *
     * Synonym for [.shouldNot]. Useful for better readability.
     *
     * For example: `$("#errorMessage").shouldNotHave(text("Exception"), text("Error"));
    ` *
     *
     * @see SelenideElement.shouldNot
     * @see com.codeborne.selenide.commands.ShouldNotHave
     */
    fun shouldNotHave(vararg condition: Condition): SelenideElement

    /**
     * Wait until given element does NOT meet given condition (with given timeout)
     */
    @kotlin.time.ExperimentalTime
    fun shouldNotHave(condition: Condition, timeout: Duration): SelenideElement

    /**
     *
     * Synonym for [.shouldNot]. Useful for better readability.
     *
     * For example: `$("#errorMessage").shouldNotBe(visible, enabled);
    ` *
     *
     * @see SelenideElement.shouldNot
     * @see com.codeborne.selenide.commands.ShouldNotBe
     */
    fun shouldNotBe(vararg condition: Condition): SelenideElement

    /**
     * Wait until given element does NOT meet given condition (with given timeout)
     */
    @kotlin.time.ExperimentalTime
    fun shouldNotBe(condition: Condition, timeout: Duration): SelenideElement

    /**
     *
     * Wait until given element meets given conditions.
     *
     *
     * IMPORTANT: in most cases you don't need this method because all should- methods wait too.
     * You need to use #waitUntil or #waitWhile methods only if you need another timeout.
     *
     * @param condition           e.g. enabled, visible, text() and so on
     * @param timeoutMilliseconds timeout in milliseconds.
     * @see com.codeborne.selenide.commands.WaitUntil
     *
     */
    @Deprecated("use {@link #shouldBe(Condition, Duration)} or {@link #shouldHave(Condition, Duration)}")
    fun waitUntil(condition: Condition, timeoutMilliseconds: Long): SelenideElement

    /**
     *
     * Wait until given element meets given conditions.
     *
     *
     * IMPORTANT: in most cases you don't need this method because all should- methods wait too.
     * You need to use #waitUntil or #waitWhile methods only if you need another timeout.
     *
     * @param condition                   e.g. enabled, visible, text() and so on
     * @param timeoutMilliseconds         timeout in milliseconds.
     * @param pollingIntervalMilliseconds interval in milliseconds, when checking condition
     * @see com.codeborne.selenide.commands.WaitUntil
     *
     */
    @Deprecated("use {@link #shouldBe(Condition, Duration)} or {@link #shouldHave(Condition, Duration)}")
    fun waitUntil(condition: Condition, timeoutMilliseconds: Long, pollingIntervalMilliseconds: Long): SelenideElement

    /**
     *
     * Wait until given element does not meet given conditions.
     *
     *
     * IMPORTANT: in most cases you don't need this method because all shouldNot- methods wait too.
     * You need to use #waitUntil or #waitWhile methods only if you need another timeout.
     *
     * @param condition           e.g. enabled, visible, text() and so on
     * @param timeoutMilliseconds timeout in milliseconds.
     * @see com.codeborne.selenide.commands.WaitWhile
     *
     */
    @Deprecated("use {@link #shouldNotBe(Condition, Duration)} or {@link #shouldNotHave(Condition, Duration)}")
    fun waitWhile(condition: Condition, timeoutMilliseconds: Long): SelenideElement

    /**
     *
     * Wait until given element does not meet given conditions.
     *
     *
     * IMPORTANT: in most cases you don't need this method because all shouldNot- methods wait too.
     * You need to use #waitUntil or #waitWhile methods only if you need another timeout.
     *
     * @param condition                   e.g. enabled, visible, text() and so on
     * @param timeoutMilliseconds         timeout in milliseconds.
     * @param pollingIntervalMilliseconds interval in milliseconds, when checking condition
     * @see com.codeborne.selenide.commands.WaitWhile
     *
     */
    @Deprecated("use {@link #shouldNotBe(Condition, Duration)} or {@link #shouldNotHave(Condition, Duration)}")
    fun waitWhile(condition: Condition, timeoutMilliseconds: Long, pollingIntervalMilliseconds: Long): SelenideElement

    /**
     * Displays WebElement in human-readable format.
     * Useful for logging and debugging.
     * Not recommended to use for test verifications.
     *
     * @return e.g. **Order has been confirmed**
     * @see com.codeborne.selenide.commands.ToString
     */
    override fun toString(): String

    /**
     * Give this element a human-readable name
     *
     * Caution: you probably don't need this method.
     * It's always a good idea to have the actual selector instead of "nice" description (which might be misleading or even lying).
     *
     * @param alias a human-readable name of this element (null or empty string not allowed)
     * @return this element
     * @since 5.17.0
     */
    fun `as`(alias: String): SelenideElement

    /**
     * Get parent element of this element
     * ATTENTION! This method doesn't start any search yet!
     * For example, $("td").parent() could give some "tr".
     *
     * @return Parent element
     * @see com.codeborne.selenide.commands.GetParent
     */
    fun parent(): SelenideElement

    /**
     * Get the following sibling element of this element
     * ATTENTION! This method doesn't start any search yet!
     * For example, $("td").sibling(0) will give the first following sibling element of "td"
     *
     * @param index the index of sibling element
     * @return Sibling element by index
     * @see com.codeborne.selenide.commands.GetSibling
     */
    fun sibling(index: Int): SelenideElement

    /**
     * Get the preceding sibling element of this element
     * ATTENTION! This method doesn't start any search yet!
     * For example, $("td").preceding(0) will give the first preceding sibling element of "td"
     *
     * @param index the index of sibling element
     * @return Sibling element by index
     * @see com.codeborne.selenide.commands.GetPreceding
     */
    fun preceding(index: Int): SelenideElement

    /**
     * Get last child element of this element
     * ATTENTION! this method doesn't start any search yet!
     * For example, $("tr").lastChild(); could give the last "td".
     */
    fun lastChild(): SelenideElement

    /**
     * Locates closes ancestor element matching given criteria
     * ATTENTION! This method doesn't start any search yet!
     * For example, $("td").closest("table") could give some "table".
     *
     * @param tagOrClass Either HTML tag or CSS class. E.g. "form" or ".active".
     * @return Matching ancestor element
     * @see com.codeborne.selenide.commands.GetClosest
     */
    fun closest(tagOrClass: String): SelenideElement

    /**
     *
     * Locates the first matching element inside given element
     * ATTENTION! This method doesn't start any search yet!
     *
     * Short form of `webElement.findElement(By.cssSelector(cssSelector))`
     *
     * @see com.codeborne.selenide.commands.Find
     */
    fun find(cssSelector: String): SelenideElement

    /**
     *
     * Locates the Nth matching element inside given element
     * ATTENTION! This method doesn't start any search yet!
     *
     * @see com.codeborne.selenide.commands.Find
     */
    fun find(cssSelector: String, index: Int): SelenideElement

    /**
     * ATTENTION! This method doesn't start any search yet!
     * Same as [.find]
     *
     * @see com.codeborne.selenide.commands.Find
     */
    fun find(selector: By): SelenideElement?

    /**
     * ATTENTION! This method doesn't start any search yet!
     * Same as [.find]
     *
     * @see com.codeborne.selenide.commands.Find
     */
    fun find(selector: By, index: Int): SelenideElement

    /**
     * ATTENTION! This method doesn't start any search yet!
     * Same as [.find]
     *
     * @see com.codeborne.selenide.commands.Find
     */
    fun `$`(cssSelector: String): SelenideElement

    /**
     * ATTENTION! This method doesn't start any search yet!
     * Same as [.find]
     *
     * @see com.codeborne.selenide.commands.Find
     */
    fun `$`(cssSelector: String, index: Int): SelenideElement

    /**
     * ATTENTION! This method doesn't start any search yet!
     * Same as [.find]
     *
     * @see com.codeborne.selenide.commands.Find
     */
    fun `$`(selector: By): SelenideElement

    /**
     * ATTENTION! This method doesn't start any search yet!
     * Same as [.find]
     *
     * @see com.codeborne.selenide.commands.Find
     */
    fun `$`(selector: By, index: Int): SelenideElement

    /**
     *
     * Locates the first matching element inside given element using xpath locator
     * ATTENTION! This method doesn't start any search yet!
     *
     * Short form of `webElement.findElement(By.xpath(xpathLocator))`
     *
     * @see com.codeborne.selenide.commands.FindByXpath
     */
    fun `$x`(xpath: String): SelenideElement

    /**
     *
     * Locates the Nth matching element inside given element using xpath locator
     * ATTENTION! This method doesn't start any search yet!
     *
     * @see com.codeborne.selenide.commands.FindByXpath
     */
    fun `$x`(xpath: String, index: Int): SelenideElement

    /**
     *
     *
     * Short form of `webDriver.findElements(thisElement, By.cssSelector(cssSelector))`
     *
     * ATTENTION! This method doesn't start any search yet!
     *
     *
     * For example, `$("#multirowTable").findAll("tr.active").shouldHave(size(2));`
     *
     *
     * @return list of elements inside given element matching given CSS selector
     * @see com.codeborne.selenide.commands.FindAll
     */
    fun findAll(cssSelector: String): ElementsCollection

    /**
     *
     *
     * Short form of `webDriver.findElements(thisElement, selector)`
     *
     * ATTENTION! This method doesn't start any search yet!
     *
     *
     * For example, `$("#multirowTable").findAll(By.className("active")).shouldHave(size(2));`
     *
     *
     * @return list of elements inside given element matching given criteria
     * @see com.codeborne.selenide.commands.FindAll
     */
    fun findAll(selector: By): ElementsCollection

    /**
     * ATTENTION! This method doesn't start any search yet!
     * Same as [.findAll]
     */
    fun `$$`(cssSelector: String): ElementsCollection

    /**
     * Same as [.findAll]
     */
    fun `$$`(selector: By): ElementsCollection

    /**
     *
     *
     * Short form of `webDriver.findElements(thisElement, By.xpath(xpath))`
     *
     * ATTENTION! This method doesn't start any search yet!
     *
     *
     * For example, `$("#multirowTable").$$x("./input").shouldHave(size(2));`
     *
     *
     * @return list of elements inside given element matching given xpath locator
     * @see com.codeborne.selenide.commands.FindAllByXpath
     */
    fun `$$x`(xpath: String): ElementsCollection

    /**
     *
     * Upload file into file upload field. File is searched from classpath.
     *
     *
     * Multiple file upload is also supported. Just pass as many file names as you wish.
     *
     * @param fileName name of the file or the relative path in classpath e.g. "files/1.pfd"
     * @return the object of the first file uploaded
     * @throws IllegalArgumentException if any of the files is not found
     * @see com.codeborne.selenide.commands.UploadFileFromClasspath
     */
//TODO:    fun uploadFromClasspath(vararg fileName: String): File

    /**
     *
     * Upload file into file upload field.
     *
     *
     * Multiple file upload is also supported. Just pass as many files as you wish.
     *
     * @param file file object(s)
     * @return the object of the first file uploaded
     * @throws IllegalArgumentException if any of the files is not found, or other errors
     * @see com.codeborne.selenide.commands.UploadFile
     */
//TODO:    fun uploadFile(vararg file: File): File

    /**
     * Select an option from dropdown list (by index)
     *
     * @param index 0..N (0 means first option)
     * @see com.codeborne.selenide.commands.SelectOptionByTextOrIndex
     */
    fun selectOption(vararg index: Int)

    /**
     * Select an option from dropdown list (by text)
     *
     * @param text visible text of option
     * @see com.codeborne.selenide.commands.SelectOptionByTextOrIndex
     */
    fun selectOption(vararg text: String)

    /**
     * Select an option from dropdown list that contains given text
     *
     * @param text substring of visible text of option
     * @see com.codeborne.selenide.commands.SelectOptionContainingText
     */
    fun selectOptionContainingText(text: String)

    /**
     * Select an option from dropdown list (by value)
     *
     * @param value "value" attribute of option
     * @see com.codeborne.selenide.commands.SelectOptionByValue
     */
    fun selectOptionByValue(vararg value: String)

    /**
     * Find (first) selected option from this select field
     *
     * @return WebElement for selected &lt;option&gt; element
     * @throws NoSuchElementException if no options are selected
     * @see com.codeborne.selenide.commands.GetSelectedOption
     */
//TODO:    @get:Throws(NoSuchElementException::class)
    val selectedOption: SelenideElement

    /**
     * Find all selected options from this select field
     *
     * @return ElementsCollection for selected &lt;option&gt; elements (empty list if no options are selected)
     * @see com.codeborne.selenide.commands.GetSelectedOptions
     */
    val selectedOptions: ElementsCollection

    /**
     * Get value of selected option in select field
     *
     * @see com.codeborne.selenide.commands.GetSelectedValue
     *
     * @return null if the selected option doesn't have "value" attribute
     */
    val selectedValue: String?

    /**
     * Get text of selected option in select field
     *
     * @see com.codeborne.selenide.commands.GetSelectedText
     */
    val selectedText: String

    /**
     * Ask browser to scroll to this element
     *
     * @see com.codeborne.selenide.commands.ScrollTo
     */
    fun scrollTo(): SelenideElement

    /**
     * Ask browser to scrolls the element on which it's called into the visible area of the browser window.
     *
     *
     * If **alignToTop** boolean value is *true* - the top of the element will be aligned to the top.
     *
     *
     * If **alignToTop** boolean value is *false* - the bottom of the element will be aligned to the bottom.
     * Usage:
     * <pre>
     * element.scrollIntoView(true);
     * // Corresponds to scrollIntoViewOptions: {block: "start", inline: "nearest"}
     *
     * element.scrollIntoView(false);
     * // Corresponds to scrollIntoViewOptions: {block: "end", inline: "nearest"}
    </pre> *
     *
     * @param alignToTop boolean value that indicate how element will be aligned to the visible area of the scrollable ancestor.
     * @see com.codeborne.selenide.commands.ScrollIntoView
     *
     * @see [Web API reference](https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView)
     */
    fun scrollIntoView(alignToTop: Boolean): SelenideElement

    /**
     * Ask browser to scrolls the element on which it's called into the visible area of the browser window.
     * <pre>
     * scrollIntoViewOptions:
     * * behavior (optional) - Defines the transition animation
     * 1. auto (default)
     * 2. instant
     * 3. smooth
     * * block (optional)
     * 1. start
     * 2. center (default)
     * 3. end
     * 4. nearest
     * * inline
     * 1. start
     * 2. center
     * 3. end
     * 4. nearest (default)
    </pre> *
     *
     *
     * Usage:
     * <pre>
     * element.scrollIntoView("{block: \"end\"}");
     * element.scrollIntoView("{behavior: \"instant\", block: \"end\", inline: \"nearest\"}");
    </pre> *
     *
     * @param scrollIntoViewOptions is an object with the align properties: behavior, block and inline.
     * @see com.codeborne.selenide.commands.ScrollIntoView
     *
     * @see [Web API reference](https://developer.mozilla.org/en-US/docs/Web/API/Element/scrollIntoView)
     */
    fun scrollIntoView(scrollIntoViewOptions: String): SelenideElement

    /**
     * Download file by clicking this element. Algorithm depends on `@{ Config#fileDownload() }`.
     *
     * @throws RuntimeException      if 50x status code was returned from server
     * @throws FileNotFoundException if 40x status code was returned from server
     * @see FileDownloadMode
     *
     * @see com.codeborne.selenide.commands.DownloadFile
     */
//TODO:    fun download(): File

    /**
     * Download file by clicking this element. Algorithm depends on `@{ Config#fileDownload() }`.
     *
     * @param timeout download operations timeout.
     * @throws RuntimeException      if 50x status code was returned from server
     * @throws FileNotFoundException if 40x status code was returned from server
     * @see com.codeborne.selenide.commands.DownloadFile
     */
//TODO:    fun download(timeout: Long): File

    /**
     * Download file by clicking this element. Algorithm depends on `@{ Config#fileDownload() }`.
     *
     * @param fileFilter Criteria for defining which file is expected (
     * [com.codeborne.selenide.files.FileFilters.withName],
     * [com.codeborne.selenide.files.FileFilters.withNameMatching],
     * [com.codeborne.selenide.files.FileFilters.withName]
     * ).
     * @throws RuntimeException      if 50x status code was returned from server
     * @throws FileNotFoundException if 40x status code was returned from server, or the downloaded file didn't match given filter.
     * @see com.codeborne.selenide.files.FileFilters
     *
     * @see com.codeborne.selenide.commands.DownloadFile
     */
//TODO:    fun download(fileFilter: FileFilter): File

    /**
     * Download file by clicking this element. Algorithm depends on `@{ Config#fileDownload() }`.
     *
     * @param timeout    download operations timeout.
     * @param fileFilter Criteria for defining which file is expected (
     * [com.codeborne.selenide.files.FileFilters.withName],
     * [com.codeborne.selenide.files.FileFilters.withNameMatching],
     * [com.codeborne.selenide.files.FileFilters.withName]
     * ).
     * @throws RuntimeException      if 50x status code was returned from server
     * @throws FileNotFoundException if 40x status code was returned from server, or the downloaded file didn't match given filter.
     * @see com.codeborne.selenide.files.FileFilters
     *
     * @see com.codeborne.selenide.commands.DownloadFile
     */
//TODO:    fun download(timeout: Long, fileFilter: FileFilter): File
//TODO:    fun download(options: DownloadOptions): File

    /**
     * Return criteria by which this element is located
     *
     * @return e.g. "#multirowTable.findBy(text 'INVALID-TEXT')/valid-selector"
     */
    val searchCriteria: String

    /**
     * @return the original Selenium [WebElement] wrapped by this object
     * @throws org.openqa.selenium.NoSuchElementException if element does not exist (without waiting for the element)
     * @see com.codeborne.selenide.commands.ToWebElement
     */
    fun toWebElement(): WebElement

    /**
     * @return Underlying [WebElement]
     * @throws com.codeborne.selenide.ex.ElementNotFound if element does not exist (after waiting for N seconds)
     * @see com.codeborne.selenide.commands.GetWrappedElement
     */
    override val wrappedElement: WebElement

    /**
     * Click the element using [ClickOptions]: `$("#username").click(ClickOptions.usingJavaScript())`
     *
     *
     *
     * You can specify a relative offset from the center of the element inside ClickOptions:
     * e.g. `$("#username").click(usingJavaScript().offset(123, 222))`
     *
     *
     * @see com.codeborne.selenide.commands.Click
     */
    fun click(clickOption: ClickOptions)

    /**
     * Click the element
     *
     *
     *
     * By default it uses default Selenium method click.
     *
     *
     *
     * But it uses JavaScript method to click if `com.codeborne.selenide.Configuration#clickViaJs` is defined.
     * It may be helpful for testing in Internet Explorer where native click doesn't always work correctly.
     *
     *
     * @see com.codeborne.selenide.commands.Click
     */
    override suspend fun click()

    /**
     * Click the element with a relative offset from the center of the element
     *
     */
    @Deprecated("use {@link #click(ClickOptions)} with offsets")
    fun click(offsetX: Int, offsetY: Int)

    /**
     * Click with right mouse button on this element
     *
     * @return this element
     * @see com.codeborne.selenide.commands.ContextClick
     */
    fun contextClick(): SelenideElement

    /**
     * Double click the element
     *
     * @return this element
     * @see com.codeborne.selenide.commands.DoubleClick
     */
    fun doubleClick(): SelenideElement

    /**
     * Emulate "mouseOver" event. In other words, move mouse cursor over this element (without clicking it).
     *
     * @return this element
     * @see com.codeborne.selenide.commands.Hover
     */
    fun hover(): SelenideElement

    /**
     * Drag and drop this element to the target
     *
     *
     * Before dropping, waits until target element gets visible.
     *
     * @param targetCssSelector CSS selector defining target element
     * @return this element
     * @see com.codeborne.selenide.commands.DragAndDropTo
     */
    fun dragAndDropTo(targetCssSelector: String): SelenideElement

    /**
     * Drag and drop this element to the target
     *
     *
     * Before dropping, waits until target element gets visible.
     *
     * @param target target element
     * @return this element
     * @see com.codeborne.selenide.commands.DragAndDropTo
     */
    fun dragAndDropTo(target: WebElement): SelenideElement

    /**
     * Drag and drop this element to the target via JS script
     * see resources/drag_and_drop_script
     *
     *
     *
     * Before dropping, waits until target element gets visible.
     *
     * @param targetCssSelector target css selector
     * @param options drag and drop options to define which way it will be executed
     *
     * @return this element
     * @see com.codeborne.selenide.commands.DragAndDropTo
     */
    fun dragAndDropTo(targetCssSelector: String, options: DragAndDropOptions): SelenideElement

    /**
     * Execute custom implemented command
     *
     * @param command custom command
     * @return whatever the command returns (incl. null)
     * @see com.codeborne.selenide.commands.Execute
     *
     * @see com.codeborne.selenide.Command
     */
    fun <ReturnType> execute(command: Command<ReturnType>): ReturnType

    /**
     * Check if image is properly loaded.
     *
     * @throws IllegalArgumentException if argument is not an "img" element
     * @see com.codeborne.selenide.commands.IsImage
     *
     * @since 2.13
     */
    val isImage: Boolean

    /**
     * Take screenshot of this element
     *
     * @return file with screenshot (*.png)
     * or null if Selenide failed to take a screenshot (due to some technical problem)
     * @see com.codeborne.selenide.commands.TakeScreenshot
     */
    @OptIn(ExperimentalFileSystem::class)
    fun screenshot(): Path?

    /**
     * Take screenshot of this element
     *
     * @return buffered image with screenshot
     * @see com.codeborne.selenide.commands.TakeScreenshotAsImage
     */
/* TODO:
    fun screenshotAsImage(): BufferedImage?
*/
}

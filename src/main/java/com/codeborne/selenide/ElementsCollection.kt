package com.codeborne.selenide

import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.UIAssertionError
import com.codeborne.selenide.impl.BySelectorCollection
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.impl.CollectionElement
import com.codeborne.selenide.impl.CollectionElementByCondition
import com.codeborne.selenide.impl.CollectionSnapshot
import com.codeborne.selenide.impl.CollectionSource
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.FilteringCollection
import com.codeborne.selenide.impl.HeadOfCollection
import com.codeborne.selenide.impl.LastCollectionElement
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.SelenideElementIterator
import com.codeborne.selenide.impl.SelenideElementListIterator
import com.codeborne.selenide.impl.TailOfCollection
import com.codeborne.selenide.impl.WebElementsCollectionWrapper
import com.codeborne.selenide.logevents.ErrorsCollector
import com.codeborne.selenide.logevents.LogEvent.EventStatus
import com.codeborne.selenide.logevents.SelenideLogger
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import java.time.Duration
import java.util.AbstractList
import java.util.stream.Collectors
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ElementsCollection(private val collection: CollectionSource) : AbstractList<SelenideElement>() {
    constructor(driver: Driver, elements: Collection<WebElement>) : this(
        WebElementsCollectionWrapper(
            driver,
            elements
        )
    ) {
    }

    constructor(driver: Driver, cssSelector: String) : this(driver, By.cssSelector(cssSelector)) {}
    constructor(driver: Driver, seleniumSelector: By) : this(BySelectorCollection(driver, seleniumSelector)) {}
    constructor(driver: Driver, parent: WebElement, cssSelector: String) : this(
        driver,
        parent,
        By.cssSelector(cssSelector)
    ) {
    }

    constructor(driver: Driver, parent: WebElement, seleniumSelector: By) : this(
        BySelectorCollection(
            driver,
            parent,
            seleniumSelector
        )
    ) {
    }

    /**
     * Deprecated. Use `$$.shouldHave(size(expectedSize))` instead.
     */
    fun shouldHaveSize(expectedSize: Int): ElementsCollection {
        return shouldHave(CollectionCondition.size(expectedSize))
    }

    /**
     * Check if a collection matches given condition(s).
     *
     *  For example:
     * `
     * $$(".text_list").should(containExactTextsCaseSensitive("text1", "text2"));
     * $$(".cat_list").should(allMatch("value==cat", el -> el.getAttribute("value").equals("cat")));
    ` *
     */
    fun should(vararg conditions: CollectionCondition): ElementsCollection {
        return should("", Duration.ofMillis(driver().config().timeout()), *conditions)
    }

    /**
     * Check if a collection matches a given condition within the given time period.
     *
     * @param timeout maximum waiting time
     */
    fun should(condition: CollectionCondition, timeout: Duration): ElementsCollection {
        return should("", timeout, *toArray(condition))
    }

    /**
     * For example: `$$(".error").shouldBe(empty)`
     */
    fun shouldBe(vararg conditions: CollectionCondition): ElementsCollection {
        return should("be", Duration.ofMillis(driver().config().timeout()), *conditions)
    }

    fun shouldBe(condition: CollectionCondition, timeout: Duration): ElementsCollection {
        return should("be", timeout, *toArray(condition))
    }

    @Deprecated("use {@link #shouldBe(CollectionCondition, Duration)}")
    fun shouldBe(condition: CollectionCondition, timeoutMs: Long): ElementsCollection {
        return should("be", Duration.ofMillis(timeoutMs), *toArray(condition))
    }

    /**
     * For example:
     * `$$(".error").shouldHave(size(3))`
     * `$$(".error").shouldHave(texts("Error1", "Error2"))`
     */
    fun shouldHave(vararg conditions: CollectionCondition): ElementsCollection {
        return should("have", Duration.ofMillis(driver().config().timeout()), *conditions)
    }

    /**
     * Check if a collection matches given condition within given period
     *
     * @param timeout maximum waiting time
     */
    fun shouldHave(condition: CollectionCondition, timeout: Duration): ElementsCollection {
        return should("have", timeout, *toArray(condition))
    }

    /**
     * Check if a collection matches given condition within given period
     *
     * @param timeoutMs maximum waiting time in milliseconds
     */
    @Deprecated("use {@link #shouldHave(CollectionCondition, Duration)}")
    fun shouldHave(condition: CollectionCondition, timeoutMs: Long): ElementsCollection {
        return should("have", Duration.ofMillis(timeoutMs), *toArray(condition))
    }

    private fun toArray(condition: CollectionCondition): Array<CollectionCondition> {
        return arrayOf(condition)
    }

    fun should(prefix: String, timeout: Duration, vararg conditions: CollectionCondition): ElementsCollection {
        ErrorsCollector.validateAssertionMode(driver().config())
        val log = SelenideLogger.beginStep(collection.description(), "should $prefix", *conditions)
        return try {
            for (condition in conditions) {
                waitUntil(condition, timeout)
            }
            SelenideLogger.commitStep(log, EventStatus.PASS)
            this
        } catch (error: Error) {
            val wrappedError = UIAssertionError.wrap(driver(), error, timeout.toMillis())
            SelenideLogger.commitStep(log, wrappedError)
            when (driver().config().assertionMode()) {
                AssertionMode.SOFT -> this
                else -> throw wrappedError
            }
        } catch (e: RuntimeException) {
            SelenideLogger.commitStep(log, e)
            throw e
        }
    }

    protected fun waitUntil(condition: CollectionCondition, timeout: Duration) {
        var lastError: Throwable? = null
        var actualElements: List<WebElement>? = null
        val stopwatch = Stopwatch(timeout.toMillis())
        do {
            try {
                actualElements = collection.elements
                if (condition.test(actualElements)) {
                    return
                }
            } catch (e: JavascriptException) {
                throw e
            } catch (elementNotFound: WebDriverException) {
                if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
                    throw Cleanup.of.wrap(elementNotFound)
                }
                if (condition.applyNull()) {
                    return
                }
                lastError = elementNotFound
            } catch (elementNotFound: IndexOutOfBoundsException) {
                if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
                    throw Cleanup.of.wrap(elementNotFound)
                }
                if (condition.applyNull()) {
                    return
                }
                lastError = elementNotFound
            } catch (elementNotFound: UIAssertionError) {
                if (Cleanup.of.isInvalidSelectorError(elementNotFound)) {
                    throw Cleanup.of.wrap(elementNotFound)
                }
                if (condition.applyNull()) {
                    return
                }
                lastError = elementNotFound
            }
            sleep(driver().config().pollingInterval())
        } while (!stopwatch.isTimeoutReached)
        if (lastError is IndexOutOfBoundsException) {
            throw ElementNotFound(collection.driver(), collection.description(), Condition.exist, lastError)
        } else if (lastError is UIAssertionError) {
            throw lastError
        } else {
            condition.fail(collection, actualElements, lastError as Exception, timeout.toMillis())
        }
    }

    fun sleep(ms: Long) {
        try {
            Thread.sleep(ms)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException(e)
        }
    }

    /**
     * Filters collection elements based on the given condition (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
     * @param condition condition
     * @return ElementsCollection
     */
    @CheckReturnValue
    fun filter(condition: Condition): ElementsCollection {
        return ElementsCollection(FilteringCollection(collection, condition))
    }

    /**
     * Filters collection elements based on the given condition (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
     * @see .filter
     * @param condition condition
     * @return ElementsCollection
     */
    @CheckReturnValue
    fun filterBy(condition: Condition): ElementsCollection {
        return filter(condition)
    }

    /**
     * Filters elements excluding those which met the given condition (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
     * @param condition condition
     * @return ElementsCollection
     */
    @CheckReturnValue
    fun exclude(condition: Condition): ElementsCollection {
        return ElementsCollection(FilteringCollection(collection, Condition.not(condition)))
    }

    /**
     * Filters elements excluding those which met the given condition (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
     * @see .exclude
     * @param condition condition
     * @return ElementsCollection
     */
    @CheckReturnValue
    fun excludeWith(condition: Condition): ElementsCollection {
        return exclude(condition)
    }

    /**
     * Find the first element which met the given condition (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
     * @param condition condition
     * @return SelenideElement
     */
    @CheckReturnValue
    fun find(condition: Condition): SelenideElement {
        return CollectionElementByCondition.wrap(collection, condition)
    }

    /**
     * Find the first element which met the given condition (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
     * @see .find
     * @param condition condition
     * @return SelenideElement
     */
    @CheckReturnValue
    fun findBy(condition: Condition): SelenideElement {
        return find(condition)
    }

    @get:CheckReturnValue
    private val elements: List<WebElement>
        get() = collection.elements

    /**
     * Gets all the texts in elements collection
     * @return array of texts
     */
    @CheckReturnValue
    fun texts(): List<String> {
        return texts(elements)
    }

    /**
     * Gets the n-th element of collection (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     *
     * @param index 0..N
     * @return the n-th element of collection
     */
    @CheckReturnValue
    override fun get(index: Int): SelenideElement {
        return CollectionElement.wrap(collection, index)
    }

    /**
     * returns the first element of the collection
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     * NOTICE: $(css) is faster and returns the same result as $$(css).first()
     * @return the first element of the collection
     */
    @CheckReturnValue
    fun first(): SelenideElement {
        return get(0)
    }

    /**
     * returns the last element of the collection (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     * @return the last element of the collection
     */
    @CheckReturnValue
    fun last(): SelenideElement {
        return LastCollectionElement.wrap(collection)
    }

    /**
     * returns the first n elements of the collection (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     * @param elements number of elements 1..N
     */
    @CheckReturnValue
    fun first(elements: Int): ElementsCollection {
        return ElementsCollection(HeadOfCollection(collection, elements))
    }

    /**
     * returns the last n elements of the collection (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     * @param elements number of elements 1..N
     */
    @CheckReturnValue
    fun last(elements: Int): ElementsCollection {
        return ElementsCollection(TailOfCollection(collection, elements))
    }

    /**
     * return actual size of the collection, doesn't wait on collection to be loaded.
     * ATTENTION not recommended for use in tests. Use collection.shouldHave(size(n)); for assertions instead.
     * @return actual size of the collection
     */
    @get:CheckReturnValue
    override val size: Int
    get() {
        return try {
            elements.size
        } catch (outOfCollection: IndexOutOfBoundsException) {
            0
        }
    }

    @CheckReturnValue
    override fun iterator(): MutableIterator<SelenideElement> {
        return SelenideElementIterator(fetch())
    }

    @CheckReturnValue
    override fun listIterator(index: Int): MutableListIterator<SelenideElement> {
        return SelenideElementListIterator(fetch(), index)
    }

    private fun fetch(): WebElementsCollectionWrapper {
        val fetchedElements = collection.elements
        return WebElementsCollectionWrapper(driver(), fetchedElements)
    }

    @CheckReturnValue
    override fun toArray(): Array<SelenideElement> {
      val fetchedElements = collection.elements
      return Array(fetchedElements.size) { i ->
        CollectionElement.wrap(collection, i)
      }
    }

    /**
     * Takes the snapshot of current state of this collection.
     * Succeeding calls to this object WILL NOT RELOAD collection element from browser.
     *
     * Use it to speed up your tests - but only if you know that collection will not be changed during the test.
     *
     * @return current state of this collection
     */
    @CheckReturnValue
    fun snapshot(): ElementsCollection {
        return ElementsCollection(CollectionSnapshot(collection))
    }

    /**
     * Give this collection a human-readable name
     *
     * Caution: you probably don't need this method.
     * It's always a good idea to have the actual selector instead of "nice" description (which might be misleading or even lying).
     *
     * @param alias a human-readable name of this collection (null or empty string not allowed)
     * @return this collection
     * @since 5.20.0
     */
    @CheckReturnValue
    fun `as`(alias: String): ElementsCollection {
        collection.setAlias(alias)
        return this
    }

    @CheckReturnValue
    override fun toString(): String {
        return try {
            String.format("%s %s", collection.description(), elementsToString(driver(), elements))
        } catch (e: RuntimeException) {
            String.format("%s [%s]", collection.description(), Cleanup.of.webdriverExceptionMessage(e))
        }
    }

    @CheckReturnValue
    private fun driver(): Driver {
        return collection.driver()
    }

    companion object {
        private val describe = Plugins.inject(
            ElementDescriber::class.java
        )

        /**
         * Fail-safe method for retrieving texts of given elements.
         * @param elements Any collection of WebElements
         * @return Array of texts (or exceptions in case of any WebDriverExceptions)
         */
        @JvmStatic
        @CheckReturnValue
            fun texts(elements: Collection<WebElement>?): List<String> {
            return if (elements == null) emptyList() else elements.stream()
                .map { element: WebElement -> getText(element) }
                .collect(Collectors.toList())
        }

        private fun getText(element: WebElement): String {
            return try {
                element.text
            } catch (elementDisappeared: WebDriverException) {
                elementDisappeared.toString()
            }
        }

        /**
         * Outputs string presentation of the element's collection
         * @param elements elements of string
         * @return String
         */
        @JvmStatic
        @CheckReturnValue
            fun elementsToString(driver: Driver, elements: Collection<WebElement>?): String {
            if (elements == null) {
                return "[not loaded yet...]"
            }
            if (elements.isEmpty()) {
                return "[]"
            }
            val sb = StringBuilder(256)
            sb.append("[").append(System.lineSeparator()).append("\t")
            for (element in elements) {
                if (sb.length > 4) {
                    sb.append(",").append(System.lineSeparator()).append("\t")
                }
                sb.append(describe.fully(driver, element))
            }
            sb.append(System.lineSeparator()).append("]")
            return sb.toString()
        }
    }
}

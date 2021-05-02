package com.codeborne.selenide

import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.UIAssertionError
import com.codeborne.selenide.impl.BySelectorCollection
import com.codeborne.selenide.impl.Cleanup
import com.codeborne.selenide.impl.CollectionElement
import com.codeborne.selenide.impl.CollectionElement.Companion.wrap
import com.codeborne.selenide.impl.CollectionElementByCondition
import com.codeborne.selenide.impl.CollectionSnapshot
import com.codeborne.selenide.impl.CollectionSource
import com.codeborne.selenide.impl.ElementDescriber
import com.codeborne.selenide.impl.FilteringCollection
import com.codeborne.selenide.impl.HeadOfCollection
import com.codeborne.selenide.impl.LastCollectionElement
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.TailOfCollection
import com.codeborne.selenide.impl.WebElementsCollectionWrapper
import com.codeborne.selenide.logevents.ErrorsCollector
import com.codeborne.selenide.logevents.LogEvent.EventStatus
import com.codeborne.selenide.logevents.SelenideLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptException
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import kotlin.time.Duration
import kotlin.time.milliseconds

class ElementsCollection(private val collection: CollectionSource) {
    constructor(driver: Driver, elements: Collection<WebElement>) : this(
        WebElementsCollectionWrapper(
            driver,
            elements
        )
    ) {
    }

    constructor(driver: Driver, cssSelector: String) : this(driver, By.cssSelector(cssSelector))
    constructor(driver: Driver, seleniumSelector: By) : this(BySelectorCollection(driver, seleniumSelector))
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
    @kotlin.time.ExperimentalTime
    suspend fun shouldHaveSize(expectedSize: Int): ElementsCollection {
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
    @kotlin.time.ExperimentalTime
    suspend fun should(vararg conditions: CollectionCondition): ElementsCollection {
        return should("", driver().config().timeout().milliseconds, *conditions)
    }

    /**
     * Check if a collection matches a given condition within the given time period.
     *
     * @param timeout maximum waiting time
     */
    @kotlin.time.ExperimentalTime
    suspend fun should(condition: CollectionCondition, timeout: Duration): ElementsCollection {
        return should("", timeout, *toArray(condition))
    }

    /**
     * For example: `$$(".error").shouldBe(empty)`
     */
    @kotlin.time.ExperimentalTime
    suspend fun shouldBe(vararg conditions: CollectionCondition): ElementsCollection {
        return should("be", driver().config().timeout().milliseconds, *conditions)
    }

    @kotlin.time.ExperimentalTime
    suspend fun shouldBe(condition: CollectionCondition, timeout: Duration): ElementsCollection {
        return should("be", timeout, *toArray(condition))
    }

    @Deprecated("use {@link #shouldBe(CollectionCondition, Duration)}")
    @kotlin.time.ExperimentalTime
    suspend fun shouldBe(condition: CollectionCondition, timeoutMs: Long): ElementsCollection {
        return should("be", timeoutMs.milliseconds, *toArray(condition))
    }

    /**
     * For example:
     * `$$(".error").shouldHave(size(3))`
     * `$$(".error").shouldHave(texts("Error1", "Error2"))`
     */
    @kotlin.time.ExperimentalTime
    suspend fun shouldHave(vararg conditions: CollectionCondition): ElementsCollection {
        return should("have", driver().config().timeout().milliseconds, *conditions)
    }

    /**
     * Check if a collection matches given condition within given period
     *
     * @param timeout maximum waiting time
     */
    @kotlin.time.ExperimentalTime
    suspend fun shouldHave(condition: CollectionCondition, timeout: Duration): ElementsCollection {
        return should("have", timeout, *toArray(condition))
    }

    /**
     * Check if a collection matches given condition within given period
     *
     * @param timeoutMs maximum waiting time in milliseconds
     */
    @Deprecated("use {@link #shouldHave(CollectionCondition, Duration)}")
    @kotlin.time.ExperimentalTime
    suspend fun shouldHave(condition: CollectionCondition, timeoutMs: Long): ElementsCollection {
        return should("have", timeoutMs.milliseconds, *toArray(condition))
    }

    private fun toArray(condition: CollectionCondition): Array<CollectionCondition> {
        return arrayOf(condition)
    }

    @kotlin.time.ExperimentalTime
    suspend fun should(prefix: String, timeout: Duration, vararg conditions: CollectionCondition): ElementsCollection {
        ErrorsCollector.validateAssertionMode(driver().config())
        val log = SelenideLogger.beginStep(collection.description(), "should $prefix", *conditions)
        return try {
            for (condition in conditions) {
                waitUntil(condition, timeout)
            }
            SelenideLogger.commitStep(log, EventStatus.PASS)
            this
        } catch (error: Error) {
            val wrappedError = UIAssertionError.wrap(driver(), error, timeout.toLongMilliseconds())
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

    @kotlin.time.ExperimentalTime
    protected suspend fun waitUntil(condition: CollectionCondition, timeout: Duration) {
        var lastError: Throwable? = null
        var actualElements: List<WebElement>? = null
        val stopwatch = Stopwatch(timeout.toLongMilliseconds())
        do {
            try {
                actualElements = collection.getElements()
                if (condition(actualElements)) {
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
            condition.fail(collection, actualElements, lastError as Exception?, timeout.toLongMilliseconds())
        }
    }

    suspend fun sleep(ms: Long) {
        delay(ms)
    }

    /**
     * Filters collection elements based on the given condition (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
     * @param condition condition
     * @return ElementsCollection
     */
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
    fun filterBy(condition: Condition): ElementsCollection {
        return filter(condition)
    }

    /**
     * Filters elements excluding those which met the given condition (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
     * @param condition condition
     * @return ElementsCollection
     */
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
    fun excludeWith(condition: Condition): ElementsCollection {
        return exclude(condition)
    }

    /**
     * Find the first element which met the given condition (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied
     * @param condition condition
     * @return SelenideElement
     */
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
    fun findBy(condition: Condition): SelenideElement {
        return find(condition)
    }

    private suspend fun getElements(): List<WebElement> = collection.getElements()

    /**
     * Gets all the texts in elements collection
     * @return array of texts
     */
    suspend fun texts(): List<String> {
        return texts(getElements())
    }

    /**
     * Gets the n-th element of collection (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     *
     * @param index 0..N
     * @return the n-th element of collection
     */
    fun get(index: Int): SelenideElement {
        return CollectionElement.wrap(collection, index)
    }

    /**
     * returns the first element of the collection
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     * NOTICE: $(css) is faster and returns the same result as $$(css).first()
     * @return the first element of the collection
     */
    fun first(): SelenideElement {
        return get(0)
    }

    /**
     * returns the last element of the collection (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     * @return the last element of the collection
     */
    fun last(): SelenideElement {
        return LastCollectionElement.wrap(collection)
    }

    /**
     * returns the first n elements of the collection (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     * @param elements number of elements 1..N
     */
    fun first(elements: Int): ElementsCollection {
        return ElementsCollection(HeadOfCollection(collection, elements))
    }

    /**
     * returns the last n elements of the collection (lazy evaluation)
     * ATTENTION! Doesn't start any search yet. Search will be started when action or assert is applied (.click(), should..() etc.)
     * @param elements number of elements 1..N
     */
    fun last(elements: Int): ElementsCollection {
        return ElementsCollection(TailOfCollection(collection, elements))
    }

    /**
     * return actual size of the collection, doesn't wait on collection to be loaded.
     * ATTENTION not recommended for use in tests. Use collection.shouldHave(size(n)); for assertions instead.
     * @return actual size of the collection
     */
    /**
     * return actual size of the collection, doesn't wait on collection to be loaded.
     * ATTENTION not recommended for use in tests. Use collection.shouldHave(size(n)); for assertions instead.
     * @return actual size of the collection
     */
    suspend fun getSize(): Int {
        return try {
            getElements().size
        } catch (outOfCollection: IndexOutOfBoundsException) {
            0
        }
    }

    suspend fun asFlow(): Flow<SelenideElement> {
        val fetchedCollection = fetch()
        return flow {
            var index = 0
            while (fetchedCollection.getElements().size > index) {
                emit(wrap(fetchedCollection, index++))
            }
        }
    }

    suspend fun asReversedFlow(): Flow<SelenideElement> {
        val fetchedCollection = fetch()
        return asReversedFlow(fetchedCollection, fetchedCollection.getElements().size)
    }

    suspend fun asReversedFlow(fromIndex: Int): Flow<SelenideElement> {
        return asReversedFlow(fetch(), fromIndex)
    }

    private suspend fun asReversedFlow(fetchedCollection: WebElementsCollectionWrapper, fromIndex: Int): Flow<SelenideElement> {
        return flow {
            var i = fromIndex
            while (i >= 0) {
                emit(wrap(fetchedCollection, --i))
            }
        }
    }

    private suspend fun fetch(): WebElementsCollectionWrapper {
        val fetchedElements = collection.getElements()
        return WebElementsCollectionWrapper(driver(), fetchedElements)
    }

    suspend fun toArray(): Array<Any?> {
        return toTypedArray() as Array<Any?>
    }

    suspend fun toTypedArray(): Array<SelenideElement> {
      val fetchedElements = collection.getElements()
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
    fun `as`(alias: String): ElementsCollection {
        collection.setAlias(alias)
        return this
    }

    // TODO: called into the actual driver in Java version
    override fun toString(): String {
        return try {
            "$collection [snapshot]"
        } catch (e: RuntimeException) {
            "$collection [${Cleanup.of.webdriverExceptionMessage(e)}]"
        }
    }
    private fun driver(): Driver {
        return collection.driver()
    }

    companion object {
        private val describe = Plugins.inject(
            ElementDescriber::class
        )

        /**
         * Fail-safe method for retrieving texts of given elements.
         * @param elements Any collection of WebElements
         * @return Array of texts (or exceptions in case of any WebDriverExceptions)
         */
            fun texts(elements: Collection<WebElement>?): List<String> {
            return elements?.map { element: WebElement -> getText(element) } ?: emptyList()
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
    suspend fun elementsToString(driver: Driver, elements: Collection<WebElement>?): String {
            if (elements == null) {
                return "[not loaded yet...]"
            }
            if (elements.isEmpty()) {
                return "[]"
            }
            val sb = StringBuilder(256)
            sb.append("[").appendLine().append("\t")
            for (element in elements) {
                if (sb.length > 4) {
                    sb.append(",").appendLine().append("\t")
                }
                sb.append(describe.fully(driver, element))
            }
            sb.appendLine().append("]")
            return sb.toString()
        }
    }
}

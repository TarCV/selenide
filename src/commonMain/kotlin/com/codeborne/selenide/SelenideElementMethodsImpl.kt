package com.codeborne.selenide

import com.codeborne.selenide.commands.Commands
import com.codeborne.selenide.impl.SelenideElementProxy
import com.codeborne.selenide.impl.SelenideElementProxy.Companion.NO_ARGS
import okio.ExperimentalFileSystem
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalFileSystem
class SelenideElementMethodsImpl(
    private val proxy: SelenideElementProxy
) : SelenideElementMethods {
    // TODO: make thread-safe
    private lateinit var element: SelenideElement

    internal fun linkElement(selenideElement: SelenideElement) {
        element = selenideElement
    }

    override suspend fun setValue(text: String): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::setValue,
        arrayOf(text)
    )

    override suspend fun `val`(text: String): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::`val`,
        arrayOf(text)
    )

    override suspend fun append(text: String): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::append,
        arrayOf(text)
    )

    override suspend fun pressEnter(): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::pressEnter,
        NO_ARGS
    )

    override suspend fun pressTab(): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::pressTab,
        NO_ARGS
    )

    override suspend fun pressEscape(): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::pressEscape,
        NO_ARGS
    )

    override suspend fun getTextAsync(): String = proxy.selenideElementInvoke(
        element,
        Commands::getText,
        NO_ARGS
    )

    override fun getAlias(): String? = proxy.selenideElementInvokeSync(
        element,
        Commands::getAlias,
        NO_ARGS
    )

    override suspend fun getOwnText(): String = proxy.selenideElementInvoke(
        element,
        Commands::getOwnText,
        NO_ARGS
    )

    override suspend fun innerText(): String = proxy.selenideElementInvoke(
        element,
        Commands::innerText,
        NO_ARGS
    )

    override suspend fun innerHtml(): String = proxy.selenideElementInvoke(
        element,
        Commands::innerHtml,
        NO_ARGS
    )

    override suspend fun attr(attributeName: String): String? = proxy.selenideElementInvoke(
        element,
        Commands::attr,
        arrayOf(attributeName)
    )

    override suspend fun name(): String? = proxy.selenideElementInvoke(
        element,
        Commands::name,
        NO_ARGS
    )

    override suspend fun `val`(): String? = proxy.selenideElementInvoke(
        element,
        Commands::getVal,
        NO_ARGS
    )

    override suspend fun getValue(): String? = proxy.selenideElementInvoke(
        element,
        Commands::getValue,
        NO_ARGS
    )

    override suspend fun pseudo(pseudoElementName: String, propertyName: String): String = proxy.selenideElementInvoke(
        element,
        Commands::pseudo,
        arrayOf(pseudoElementName, propertyName)
    )

    override suspend fun pseudo(pseudoElementName: String): String = proxy.selenideElementInvoke(
        element,
        Commands::pseudo,
        arrayOf(pseudoElementName)
    )

    override suspend fun selectRadio(value: String): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::selectRadio,
        arrayOf(value)
    )

    override suspend fun data(dataAttributeName: String): String? = proxy.selenideElementInvoke(
        element,
        Commands::data,
        arrayOf(dataAttributeName)
    )

    override suspend fun getAttributeAsync(name: String): String? = proxy.selenideElementInvoke(
        element,
        Commands::getAttribute,
        arrayOf(name)
    )

    override suspend fun getCssValueAsync(propertyName: String): String = proxy.selenideElementInvoke(
        element,
        Commands::getCssValue,
        arrayOf(propertyName)
    )

    override suspend fun exists(): Boolean = proxy.selenideElementInvoke(
        element,
        Commands::exists,
        NO_ARGS
    )

    override suspend fun isDisplayedAsync(): Boolean = proxy.selenideElementInvoke(
        element,
        Commands::isDisplayed,
        NO_ARGS
    )

    override suspend fun `is`(condition: Condition): Boolean = proxy.selenideElementInvoke(
        element,
        Commands::`is`,
        arrayOf(condition)
    )

    override suspend fun has(condition: Condition): Boolean = proxy.selenideElementInvoke(
        element,
        Commands::has,
        arrayOf(condition)
    )

    override suspend fun setSelected(selected: Boolean): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::setSelected,
        arrayOf(selected)
    )

    override suspend fun should(vararg condition: Condition): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::should,
        condition
    )

    @ExperimentalTime
    override suspend fun should(condition: Condition, timeout: Duration): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::should,
        arrayOf(condition, timeout)
    )

    override suspend fun shouldHave(vararg condition: Condition): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldHave,
        condition
    )

    @ExperimentalTime
    override suspend fun shouldHave(condition: Condition, timeout: Duration): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldHave,
        arrayOf(condition, timeout)
    )

    override suspend fun shouldBe(vararg condition: Condition): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldBe,
        condition
    )

    @ExperimentalTime
    override suspend fun shouldBe(condition: Condition, timeout: Duration): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldBe,
        arrayOf(condition, timeout)
    )

    override suspend fun shouldNot(vararg condition: Condition): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldNot,
        condition
    )

    @ExperimentalTime
    override suspend fun shouldNot(condition: Condition, timeout: Duration): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldNot,
        arrayOf(condition, timeout)
    )

    override suspend fun shouldNotHave(vararg condition: Condition): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldNotHave,
        condition
    )

    @ExperimentalTime
    override suspend fun shouldNotHave(condition: Condition, timeout: Duration): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldNotHave,
        arrayOf(condition, timeout)
    )

    override suspend fun shouldNotBe(vararg condition: Condition): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldNotBe,
        condition
    )

    @ExperimentalTime
    override suspend fun shouldNotBe(condition: Condition, timeout: Duration): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::shouldNotBe,
        arrayOf(condition, timeout)
    )

    override suspend fun waitUntil(condition: Condition, timeoutMilliseconds: Long): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::waitUntil,
        arrayOf(condition, timeoutMilliseconds)
    )

    override suspend fun waitUntil(
        condition: Condition,
        timeoutMilliseconds: Long,
        pollingIntervalMilliseconds: Long
    ): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::waitUntil,
        arrayOf(condition, timeoutMilliseconds, pollingIntervalMilliseconds)
    )

    override suspend fun waitWhile(condition: Condition, timeoutMilliseconds: Long): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::waitWhile,
        arrayOf(condition, timeoutMilliseconds)
    )

    override suspend fun waitWhile(
        condition: Condition,
        timeoutMilliseconds: Long,
        pollingIntervalMilliseconds: Long
    ): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::waitWhile,
        arrayOf(condition, timeoutMilliseconds, pollingIntervalMilliseconds)
    )

    override suspend fun describe(): String = proxy.selenideElementInvoke(
        element,
        Commands::toString,
        NO_ARGS
    )

    override fun `as`(alias: String): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::`as`,
        arrayOf(alias)
    )

    override fun parent(): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::parent,
        NO_ARGS
    )

    override fun sibling(index: Int): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::sibling,
        arrayOf(index)
    )

    override fun preceding(index: Int): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::preceding,
        arrayOf(index)
    )

    override fun lastChild(): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::lastChild,
        NO_ARGS
    )

    override fun closest(tagOrClass: String): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::closest,
        arrayOf(tagOrClass)
    )

    override fun find(cssSelector: String): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::find,
        arrayOf(cssSelector)
    )

    override fun find(cssSelector: String, index: Int): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::find,
        arrayOf(cssSelector, index)
    )

    override fun find(selector: org.openqa.selenium.By): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::find,
        arrayOf(selector)
    )

    override fun find(selector: org.openqa.selenium.By, index: Int): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::find,
        arrayOf(selector, index)
    )

    override fun `$`(cssSelector: String): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::`$`,
        arrayOf(cssSelector)
    )

    override fun `$`(cssSelector: String, index: Int): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::`$`,
        arrayOf(cssSelector, index)
    )

    override fun `$`(selector: org.openqa.selenium.By): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::`$`,
        arrayOf(selector)
    )

    override fun `$`(selector: org.openqa.selenium.By, index: Int): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::`$`,
        arrayOf(selector, index)
    )

    override fun `$x`(xpath: String): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::`$x`,
        arrayOf(xpath)
    )

    override fun `$x`(xpath: String, index: Int): SelenideElement = proxy.selenideElementInvokeSync(
        element,
        Commands::`$x`,
        arrayOf(xpath, index)
    )

    override fun findAll(cssSelector: String): ElementsCollection = proxy.selenideElementInvokeSync(
        element,
        Commands::findAll,
        arrayOf(cssSelector)
    )

    override fun findAll(selector: org.openqa.selenium.By): ElementsCollection = proxy.selenideElementInvokeSync(
        element,
        Commands::findAll,
        arrayOf(selector)
    )

    override fun `$$`(cssSelector: String): ElementsCollection = proxy.selenideElementInvokeSync(
        element,
        Commands::`$$`,
        arrayOf(cssSelector)
    )

    override fun `$$`(selector: org.openqa.selenium.By): ElementsCollection = proxy.selenideElementInvokeSync(
        element,
        Commands::`$$`,
        arrayOf(selector)
    )

    override fun `$$x`(xpath: String): ElementsCollection = proxy.selenideElementInvokeSync(
        element,
        Commands::`$$x`,
        arrayOf(xpath)
    )

    override suspend fun selectOption(vararg index: Int) = proxy.selenideElementInvoke(
        element,
        Commands::selectOption,
        index.toTypedArray()
    )

    override suspend fun selectOption(vararg text: String): Unit = proxy.selenideElementInvoke(
        element,
        Commands::selectOption,
        text
    )

    override suspend fun selectOptionContainingText(text: String): Unit = proxy.selenideElementInvoke(
        element,
        Commands::selectOptionContainingText,
        arrayOf(text)
    )

    override suspend fun selectOptionByValue(vararg value: String): Unit = proxy.selenideElementInvoke(
        element,
        Commands::selectOptionByValue,
        value
    )

    override suspend fun getSelectedOption(): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::getSelectedOption,
        NO_ARGS
    )

    override suspend fun getSelectedOptions(): ElementsCollection = proxy.selenideElementInvoke(
        element,
        Commands::getSelectedOptions,
        NO_ARGS
    )

    override suspend fun getSelectedValue(): String? = proxy.selenideElementInvoke(
        element,
        Commands::getSelectedValue,
        NO_ARGS
    )

    override suspend fun getSelectedText(): String = proxy.selenideElementInvoke(
        element,
        Commands::getSelectedText,
        NO_ARGS
    )

    override suspend fun scrollTo(): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::scrollTo,
        NO_ARGS
    )

    override suspend fun scrollIntoView(alignToTop: Boolean): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::scrollIntoView,
        arrayOf(alignToTop)
    )

    override suspend fun scrollIntoView(scrollIntoViewOptions: String): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::scrollIntoView,
        arrayOf(scrollIntoViewOptions)
    )

    override val searchCriteria: String
        get() = proxy.selenideElementInvokeSync(
            element,
            Commands::getSearchCriteria,
            NO_ARGS
        )

    override suspend fun toWebElement(): org.openqa.selenium.WebElement = proxy.selenideElementInvoke(
        element,
        Commands::toWebElement,
        NO_ARGS
    )

    override suspend fun getWrappedElementAsync(): org.openqa.selenium.WebElement = proxy.selenideElementInvoke(
        element,
        Commands::getWrappedElement,
        NO_ARGS
    )

    override suspend fun click(clickOption: ClickOptions) = proxy.selenideElementInvoke(
        element,
        Commands::click,
        arrayOf(clickOption)
    )

    override suspend fun clickAsync() = proxy.selenideElementInvoke(
        element,
        Commands::click,
        NO_ARGS
    )

    override suspend fun click(offsetX: Int, offsetY: Int) = proxy.selenideElementInvoke(
        element,
        Commands::click,
        arrayOf(offsetX, offsetY)
    )

    override suspend fun contextClick(): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::contextClick,
        NO_ARGS
    )

    override suspend fun doubleClick(): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::doubleClick,
        NO_ARGS
    )

    override suspend fun hover(): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::hover,
        NO_ARGS
    )

    override suspend fun dragAndDropTo(targetCssSelector: String): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::dragAndDropTo,
        arrayOf(targetCssSelector)
    )

    override suspend fun dragAndDropTo(target: org.openqa.selenium.WebElement): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::dragAndDropTo,
        arrayOf(target)
    )

    override suspend fun dragAndDropTo(targetCssSelector: String, options: DragAndDropOptions): SelenideElement = proxy.selenideElementInvoke(
        element,
        Commands::dragAndDropTo,
        arrayOf(targetCssSelector, options)
    )

    override suspend fun <ReturnType> execute(command: Command<ReturnType>): ReturnType = proxy.selenideElementInvoke(
        element,
        Commands::execute,
        arrayOf(command)
    ) as ReturnType

    override suspend fun isImage(): Boolean = proxy.selenideElementInvoke(
        element,
        Commands::isImage,
        NO_ARGS
    )
}

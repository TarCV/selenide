package com.codeborne.selenide.impl

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsContainer
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.PageObjectException
import com.codeborne.selenide.impl.ElementFinder.Companion.wrap
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.SearchContext
import java.lang.reflect.Field
import java.lang.reflect.Type
import java.util.AbstractList
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ElementsContainerCollection(
    private val pageFactory: PageObjectFactory,
    private val driver: Driver,
    private val parent: SearchContext,
    private val field: Field,
    private val listType: Class<*>,
    private val genericTypes: Array<Type>,
    private val selector: By
) : AbstractList<ElementsContainer>() {
    @CheckReturnValue
    override fun get(index: Int): ElementsContainer {
        val self: SelenideElement = wrap(driver, parent, selector, index)
        return try {
            pageFactory.initElementsContainer(driver, field, self, listType, genericTypes)
        } catch (e: ReflectiveOperationException) {
            throw PageObjectException("Failed to initialize field $field", e)
        }
    }

    @get:CheckReturnValue
    override val size: Int
        get() {
            return try {
                WebElementSelector.instance.findElements(driver, parent, selector).size
            } catch (e: NoSuchElementException) {
                throw ElementNotFound(driver, selector.toString(), Condition.exist, e)
            }
        }
}

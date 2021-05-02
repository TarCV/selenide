package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.ElementsContainer
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.PageObjectException
import com.codeborne.selenide.impl.ElementFinder.Companion.wrap
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.FindBys
import org.openqa.selenium.support.pagefactory.Annotations
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator
import org.slf4j.LoggerFactory
import support.reflect.Modifier
import support.reflect.ParameterizedType
import support.reflect.TypeVariable

/**
 * Factory class to make using Page Objects simpler and easier.
 *
 * @see [Page Objects Wiki](https://github.com/SeleniumHQ/selenium/wiki/PageObjects)
 */
open class SelenidePageFactory : PageObjectFactory {
    override fun <PageObjectClass : Any> page(driver: Driver, pageObjectClass: kotlin.reflect.KClass<PageObjectClass>): PageObjectClass {
        return try {
            val constructor = pageObjectClass.getDeclaredConstructor()
            constructor.isAccessible = true
            page(driver, constructor.newInstance())
        } catch (e: ReflectiveOperationException) {
            throw PageObjectException("Failed to create new instance of $pageObjectClass", e)
        }
    }
    override fun <PageObjectClass : Any, T : PageObjectClass> page(driver: Driver, pageObject: T): PageObjectClass {
        val types: Array<Type> = pageObject::class.genericInterfaces
        initElements(driver, driver.webDriver, pageObject, types)
        return pageObject
    }

    /**
     * Similar to the other "initElements" methods, but takes an [FieldDecorator] which is used
     * for decorating each of the fields.
     *
     * @param page      The object to decorate the fields of
     */
    fun initElements(driver: Driver, searchContext: SearchContext, page: Any, genericTypes: Array<Type>) {
        var proxyIn: kotlin.reflect.KClass<*> = page::class
        while (proxyIn != Any::class) {
            initFields(driver, searchContext, page, proxyIn, genericTypes)
            proxyIn = proxyIn.superclass
        }
    }

    protected fun initFields(
        driver: Driver, searchContext: SearchContext,
        page: Any, proxyIn: kotlin.reflect.KClass<*>, genericTypes: Array<Type>
    ) {
        val fields = proxyIn.declaredFields
        for (field in fields) {
            if (!isInitialized(page, field)) {
                val selector = findSelector(driver, field)
                val value = decorate( null, driver, searchContext, field, selector, genericTypes)
                value?.let { setFieldValue(page, field, it) }
            }
        }
    }

    protected fun findSelector(driver: Driver?, field: Field?): By {
        return Annotations(field).buildBy()
    }

    protected fun setFieldValue(page: Any?, field: Field, value: Any) {
        try {
            field.isAccessible = true
            field[page] = value
        } catch (e: IllegalAccessException) {
            throw PageObjectException("Failed to assign field $field to value $value", e)
        }
    }
    protected fun isInitialized(page: Any, field: Field): Boolean {
        return try {
            field.isAccessible = true
            field[page] != null
        } catch (e: IllegalAccessException) {
            throw PageObjectException("Failed to access field $field in $page", e)
        }
    }
    override fun createElementsContainer(
      driver: Driver,
      searchContext: SearchContext?,
      field: Field,
      selector: By
    ): ElementsContainer {
        return try {
            val self = wrap(driver, searchContext, selector, 0)
            initElementsContainer(driver, field, self)
        } catch (e: ReflectiveOperationException) {
            throw PageObjectException("Failed to create elements container for field " + field.name, e)
        }
    }
    fun initElementsContainer(driver: Driver, field: Field, self: SelenideElement): ElementsContainer {
        val genericTypes =
            if (field.genericType is ParameterizedType) (field.genericType as ParameterizedType).actualTypeArguments else arrayOfNulls(
                0
            )
        return initElementsContainer(driver, field, self, field.type, genericTypes)
    }
    override fun initElementsContainer(
      driver: Driver,
      field: Field?,
      self: SelenideElement,
      type: kotlin.reflect.KClass<*>,
      genericTypes: Array<Type>
    ): ElementsContainer {
        require(!Modifier.isInterface(type.modifiers)) { "Cannot initialize field $field: $type is interface" }
        require(!Modifier.isAbstract(type.modifiers)) { "Cannot initialize field $field: $type is abstract" }
        val constructor = type.getDeclaredConstructor()
        constructor.isAccessible = true
        val result = constructor.newInstance() as ElementsContainer
        initElements(driver, self, result, genericTypes)
        return result
    }
    fun decorate(
        loader: ClassLoader?,
        driver: Driver, searchContext: SearchContext,
        field: Field, selector: By
    ): Any? {
        val classGenericTypes = field.declaringClass.genericInterfaces
        return decorate(loader, driver, searchContext, field, selector, classGenericTypes)
    }
    fun decorate(
        loader: ClassLoader?,
        driver: Driver, searchContext: SearchContext,
        field: Field, selector: By, genericTypes: Array<Type>
    ): Any? {
        if (ElementsContainer::class == field.declaringClass && "self" == field.name) {
            return if (searchContext is SelenideElement) {
                searchContext
            } else {
                logger.warn("Cannot initialize field ${}", field)
                null
            }
        }
        if (WebElement::class.isInstance(field.type)) {
            return wrap(driver, searchContext, selector, 0)
        }
        if (ElementsCollection::class.isInstance(field.type) ||
            isDecoratableList(field, genericTypes, WebElement::class)
        ) {
            return ElementsCollection(BySelectorCollection(driver, searchContext, selector))
        } else if (ElementsContainer::class.isInstance(field.type)) {
            return createElementsContainer(driver, searchContext, field, selector)
        } else if (isDecoratableList(field, genericTypes, ElementsContainer::class)) {
            return createElementsContainerList(driver, searchContext, field, genericTypes, selector)
        }
        return defaultFieldDecorator(searchContext).decorate(loader, field)
    }
    protected fun defaultFieldDecorator(searchContext: SearchContext?): DefaultFieldDecorator {
        return DefaultFieldDecorator(DefaultElementLocatorFactory(searchContext))
    }
    protected fun createElementsContainerList(
        driver: Driver, searchContext: SearchContext,
        field: Field, genericTypes: Array<Type>, selector: By
    ): List<ElementsContainer> {
        val listType = getListGenericType(field, genericTypes)
            ?: throw IllegalArgumentException("Cannot detect list type for $field")
        return ElementsContainerCollection(this, driver, searchContext, field, listType, genericTypes, selector)
    }
    protected fun isDecoratableList(field: Field, genericTypes: Array<Type>, type: kotlin.reflect.KClass<*>): Boolean {
        if (!MutableList::class.isInstance(field.type)) {
            return false
        }
        val listType = getListGenericType(field, genericTypes)
        return (listType != null && type.isInstance(listType)
                && (field.getAnnotation(FindBy::class) != null || field.getAnnotation(FindBys::class) != null))
    }
    protected fun getListGenericType(field: Field, genericTypes: Array<Type>): kotlin.reflect.KClass<*>? {
        val fieldType = field.genericType as? ParameterizedType ?: return null
        val actualTypeArguments = fieldType.actualTypeArguments
        val firstType = actualTypeArguments[0]
        if (firstType is TypeVariable<*>) {
            val indexOfType = indexOf(field.declaringClass, firstType)
            return genericTypes[indexOfType] as kotlin.reflect.KClass<*>
        } else if (firstType is kotlin.reflect.KClass<*>) {
            return firstType
        }
        throw IllegalArgumentException("Cannot detect list type of $field")
    }

    protected fun indexOf(klass: kotlin.reflect.KClass<*>, firstArgument: Type): Int {
        val objects = Arrays.stream(klass.typeParameters).toArray()
        for (i in objects.indices) {
            if (objects[i] == firstArgument) return i
        }
        return -1
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SelenidePageFactory::class)
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsContainer
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import java.lang.reflect.Field
import java.lang.reflect.Type
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
interface PageObjectFactory {
    fun <PageObjectClass : Any> page(driver: Driver, pageObjectClass: Class<PageObjectClass>): PageObjectClass
    fun <PageObjectClass : Any, T : PageObjectClass> page(driver: Driver, pageObject: T): PageObjectClass
    fun createElementsContainer(
        driver: Driver?,
        searchContext: SearchContext?,
        field: Field?,
        selector: By?
    ): ElementsContainer

    @Throws(ReflectiveOperationException::class)
    fun initElementsContainer(
        driver: Driver?,
        field: Field?,
        self: SelenideElement?,
        type: Class<*>,
        genericTypes: Array<Type>
    ): ElementsContainer
}

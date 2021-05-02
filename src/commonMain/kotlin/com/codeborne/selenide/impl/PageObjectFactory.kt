package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsContainer
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext

interface PageObjectFactory {
    fun <PageObjectClass : Any> page(driver: Driver, pageObjectClass: kotlin.reflect.KClass<PageObjectClass>): PageObjectClass
    fun <PageObjectClass : Any, T : PageObjectClass> page(driver: Driver, pageObject: T): PageObjectClass
    fun createElementsContainer(
        driver: Driver,
        searchContext: org.openqa.selenium.SearchContext?,
        field: kotlin.reflect.KProperty<*>,
        selector: org.openqa.selenium.By
    ): ElementsContainer

    fun initElementsContainer(
        driver: Driver,
        field: kotlin.reflect.KProperty<*>?,
        self: SelenideElement,
        type: kotlin.reflect.KClass<*>,
        genericTypes: Array<kotlin.reflect.KType>
    ): ElementsContainer
}

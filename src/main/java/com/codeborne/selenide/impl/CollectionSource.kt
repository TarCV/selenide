package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
interface CollectionSource {
    /**
     * get elements of this collection (probably cached).
     */
    @get:CheckReturnValue
    val elements: List<WebElement>

    /**
     * get Nth element of this collection
     */
    @CheckReturnValue
    fun getElement(index: Int): WebElement

    @CheckReturnValue
    fun description(): String

    @CheckReturnValue
    fun driver(): Driver

    fun setAlias(alias: String)
}

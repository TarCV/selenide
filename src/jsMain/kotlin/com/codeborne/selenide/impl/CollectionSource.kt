package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

interface CollectionSource {
    /**
     * get elements of this collection (probably cached).
     */
    suspend fun getElements(): List<WebElement>

    /**
     * get Nth element of this collection
     */
    suspend fun getElement(index: Int): WebElement
    suspend fun description(): String
    fun driver(): Driver

    fun setAlias(alias: String)
}

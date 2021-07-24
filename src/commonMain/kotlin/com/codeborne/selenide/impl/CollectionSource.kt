package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

interface CollectionSource {
    /**
     * get elements of this collection (probably cached).
     */
    suspend fun getElements(): List<org.openqa.selenium.WebElement>

    /**
     * get Nth element of this collection
     */
    suspend fun getElement(index: Int): org.openqa.selenium.WebElement
    fun description(): String
    fun driver(): Driver

    fun setAlias(alias: String)
}

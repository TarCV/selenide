package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

interface ElementDescriber {
    suspend fun fully(driver: Driver, element: WebElement?): String
    suspend fun briefly(driver: Driver, element: WebElement): String
    fun selector(selector: By?): String
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

interface ElementDescriber {
    suspend fun fully(driver: Driver, element: org.openqa.selenium.WebElement?): String
    suspend fun briefly(driver: Driver, element: org.openqa.selenium.WebElement): String
    fun selector(selector: org.openqa.selenium.By?): String
}

package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

class Attribute(private val attributeName: String) : Condition("attribute") {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return element.getAttribute(attributeName) != null
    }

    override fun toString(): String {
        return "$name $attributeName"
    }
}

package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Attribute(private val attributeName: String) : Condition("attribute") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return element.getAttribute(attributeName) != null
    }

    override fun toString(): String {
        return "$name $attributeName"
    }
}

package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class CssClass(private val expectedCssClass: String) : Condition("css class") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        val actualCssClasses = element.getAttribute("class")
        return actualCssClasses != null && contains(actualCssClasses.split(" ").toTypedArray(), expectedCssClass)
    }

    override fun toString(): String {
        return String.format("%s '%s'", name, expectedCssClass)
    }

    private fun <T> contains(objects: Array<T>, `object`: T): Boolean {
        for (object1 in objects) {
            if (`object` == object1) {
                return true
            }
        }
        return false
    }
}

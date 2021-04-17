package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Checked : Condition("checked") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return element.isSelected
    }

    override fun actualValue(driver: Driver, element: WebElement): String {
        return element.isSelected.toString()
    }
}

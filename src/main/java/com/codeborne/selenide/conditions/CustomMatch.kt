package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import java.util.function.Predicate
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class CustomMatch(description: String, protected val predicate: Predicate<WebElement>) : Condition(
  description
) {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return predicate.test(element)
    }

    override fun toString(): String {
        return String.format("match '%s' predicate.", name)
    }
}

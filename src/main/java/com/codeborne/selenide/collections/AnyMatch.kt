package com.codeborne.selenide.collections

import org.openqa.selenium.WebElement
import java.util.function.Predicate
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class AnyMatch(description: String, predicate: Predicate<WebElement>) :
    PredicateCollectionCondition("any", description, predicate) {
    override fun test(elements: List<WebElement>): Boolean {
        return elements.stream().anyMatch(predicate)
    }
}

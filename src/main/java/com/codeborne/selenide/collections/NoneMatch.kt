package com.codeborne.selenide.collections

import org.openqa.selenium.WebElement
import java.util.function.Predicate
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class NoneMatch(description: String, predicate: Predicate<WebElement>) :
    PredicateCollectionCondition("none", description, predicate) {
    override fun test(elements: List<WebElement>): Boolean {
        return if (elements.isEmpty()) {
            false
        } else elements.stream().noneMatch(predicate)
    }
}

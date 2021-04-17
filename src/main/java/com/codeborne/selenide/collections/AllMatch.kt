package com.codeborne.selenide.collections

import org.openqa.selenium.WebElement
import java.util.function.Predicate
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class AllMatch(description: String, predicate: Predicate<WebElement>) :
    PredicateCollectionCondition("all", description, predicate) {
    override fun test(elements: List<WebElement>): Boolean {
        return if (elements.isEmpty()) {
            false
        } else elements.stream().allMatch(predicate)
    }
}

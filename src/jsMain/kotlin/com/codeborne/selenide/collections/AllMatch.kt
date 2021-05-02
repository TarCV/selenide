package com.codeborne.selenide.collections

import org.openqa.selenium.WebElement

class AllMatch(description: String, predicate: (WebElement) -> Boolean) :
    PredicateCollectionCondition("all", description, predicate) {
    override operator fun invoke(elements: List<WebElement>): Boolean {
        return if (elements.isEmpty()) {
            false
        } else elements.all(predicate)
    }
}

package com.codeborne.selenide.collections

import org.openqa.selenium.WebElement

class NoneMatch(description: String, predicate: (WebElement) -> Boolean) :
    PredicateCollectionCondition("none", description, predicate) {
    override operator fun invoke(elements: List<WebElement>): Boolean {
        return if (elements.isEmpty()) {
            false
        } else elements.none(predicate)
    }
}

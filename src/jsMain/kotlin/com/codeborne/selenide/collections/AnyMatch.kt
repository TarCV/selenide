package com.codeborne.selenide.collections

import org.openqa.selenium.WebElement

class AnyMatch(description: String, predicate: (WebElement) -> Boolean) :
    PredicateCollectionCondition("any", description, predicate) {
    override operator fun invoke(elements: List<WebElement>): Boolean {
        return elements.anyMatch(predicate)
    }
}

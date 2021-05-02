package com.codeborne.selenide.collections

import org.openqa.selenium.WebElement

class AnyMatch(description: String, predicate: (org.openqa.selenium.WebElement) -> Boolean) :
    PredicateCollectionCondition("any", description, predicate) {
    override fun test(elements: List<org.openqa.selenium.WebElement>): Boolean {
        return elements.any(predicate)
    }
}

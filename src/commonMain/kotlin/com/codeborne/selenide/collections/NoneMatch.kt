package com.codeborne.selenide.collections

import org.openqa.selenium.WebElement

class NoneMatch(description: String, predicate: (org.openqa.selenium.WebElement) -> Boolean) :
    PredicateCollectionCondition("none", description, predicate) {
    override suspend fun test(elements: List<org.openqa.selenium.WebElement>): Boolean {
        return if (elements.isEmpty()) {
            false
        } else elements.none(predicate)
    }
}

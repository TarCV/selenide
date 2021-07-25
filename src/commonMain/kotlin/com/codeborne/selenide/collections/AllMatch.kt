package com.codeborne.selenide.collections

import org.openqa.selenium.WebElement

class AllMatch(description: String, predicate: (org.openqa.selenium.WebElement) -> Boolean) :
    PredicateCollectionCondition("all", description, predicate) {
    override suspend fun test(elements: List<org.openqa.selenium.WebElement>): Boolean {
        return if (elements.isEmpty()) {
            false
        } else elements.all(predicate)
    }
}

package org.openqa.selenium.support.ui

import org.openqa.selenium.WebDriver

expect interface ExpectedCondition<T> {
    suspend operator fun invoke(driver: org.openqa.selenium.WebDriver): T?
}

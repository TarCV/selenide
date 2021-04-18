package org.openqa.selenium.support.ui

import org.openqa.selenium.WebDriver

interface ExpectedCondition<T> {
    suspend operator fun invoke(driver: WebDriver): T?
}

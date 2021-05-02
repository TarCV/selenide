package org.openqa.selenium.support.ui

import org.openqa.selenium.WebDriver

actual interface ExpectedCondition<T> {
    actual suspend operator fun invoke(driver: org.openqa.selenium.WebDriver): T?
}

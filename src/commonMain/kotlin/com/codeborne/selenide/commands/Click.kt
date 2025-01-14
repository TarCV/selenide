package com.codeborne.selenide.commands

import com.codeborne.selenide.ClickMethod
import com.codeborne.selenide.ClickOptions
import com.codeborne.selenide.Command
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.filecontent.ClickJs.clickJs
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement

open class Click : Command<Unit> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): Unit {
        val driver = locator.driver()
        val webElement = locator.findAndAssertElementIsInteractable()
        if (args == null || args.isEmpty()) {
            click(driver, webElement)
        } else if (args.size == 1) {
            val clickOptions = Util.firstOf<ClickOptions>(args)
            click(driver, webElement, clickOptions)
        } else if (args.size == 2) {
            val offsetX = Util.firstOf<Int>(args)
            val offsetY = args[1] as Int
            click(driver, webElement, offsetX, offsetY)
        }
    }

    open suspend fun click(driver: Driver, element: org.openqa.selenium.WebElement) {
        if (driver.config().clickViaJs()) {
            click(driver, element, 0, 0)
        } else {
            element.click()
        }
    }

    // should be removed after deleting SelenideElement.click(int offsetX, int offsetY);
    protected suspend fun click(driver: Driver, element: org.openqa.selenium.WebElement, offsetX: Int, offsetY: Int) {
        if (driver.config().clickViaJs()) {
            clickViaJS(driver, element, offsetX, offsetY)
        } else {
            driver.actions()
                .moveToElement(element, offsetX, offsetY)
                .click()
                .build()
                .perform()
        }
    }

    private suspend fun click(driver: Driver, webElement: org.openqa.selenium.WebElement, clickOptions: ClickOptions) {
        when (clickOptions.clickOption()) {
            ClickMethod.DEFAULT -> {
                defaultClick(driver, webElement, clickOptions.offsetX(), clickOptions.offsetY())
            }
            ClickMethod.JS -> {
                clickViaJS(driver, webElement, clickOptions.offsetX(), clickOptions.offsetY())
            }
            else -> {
                throw IllegalArgumentException("Unknown click option: " + clickOptions.clickOption())
            }
        }
    }

    private fun defaultClick(driver: Driver, element: org.openqa.selenium.WebElement, offsetX: Int, offsetY: Int) {
        driver.actions()
            .moveToElement(element, offsetX, offsetY)
            .click()
            .perform()
    }

    private suspend fun clickViaJS(driver: Driver, element: org.openqa.selenium.WebElement, offsetX: Int, offsetY: Int) {
        driver.executeJavaScript<Any>(clickJs, element, offsetX, offsetY)
    }
}

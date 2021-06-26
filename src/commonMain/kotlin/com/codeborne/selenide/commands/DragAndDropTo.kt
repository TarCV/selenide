package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Condition
import com.codeborne.selenide.DragAndDropOptions
import com.codeborne.selenide.DragAndDropOptions.Companion.usingJavaScript
import com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.filecontent.DragAndDropJs.dragAndDropJs
import com.codeborne.selenide.impl.Arguments
import com.codeborne.selenide.impl.ElementFinder
import com.codeborne.selenide.impl.WebElementSource
import com.codeborne.selenide.impl.WebElementWrapper
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions

open class DragAndDropTo : Command<SelenideElement> {
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
        val target = findTarget(locator.driver(), args)
        target.shouldBe(Condition.visible)
        val options = Arguments(args)
            .ofType(DragAndDropOptions::class)
            ?: usingJavaScript()
        dragAndDrop(locator, target, options)
        return proxy
    }

    protected fun findTarget(driver: Driver, args: Array<out Any?>?): SelenideElement {
        return if (args == null || args.isEmpty()) {
            throw IllegalArgumentException("Missing target argument")
        } else if (args[0] is String) {
            ElementFinder.wrap(driver, org.openqa.selenium.By.cssSelector(args[0] as String))
        } else if (args[0] is org.openqa.selenium.WebElement) {
            WebElementWrapper.wrap(driver, args[0] as org.openqa.selenium.WebElement)
        } else {
            throw IllegalArgumentException(
                "Unknown target type: " + args[0] +
                        " (only String or WebElement are supported)"
            )
        }
    }

    private suspend fun dragAndDrop(locator: WebElementSource, target: SelenideElement, options: DragAndDropOptions) {
        when (options.method) {
            DragAndDropMethod.JS -> dragAndDropUsingJavaScript(
                locator.driver(),
                locator.getWebElement(),
                target.getWrappedElement()
            )
            DragAndDropMethod.ACTIONS -> dragAndDropUsingActions(
                locator.driver(),
                locator.getWebElement(),
                target.getWrappedElement()
            )
            else -> throw IllegalArgumentException("Drag and Drop method not defined!")
        }
    }

    private fun dragAndDropUsingActions(driver: Driver, from: org.openqa.selenium.WebElement, target: org.openqa.selenium.WebElement) {
        org.openqa.selenium.interactions.Actions(driver.webDriver).dragAndDrop(from, target).perform()
    }

    private suspend fun dragAndDropUsingJavaScript(driver: Driver, from: org.openqa.selenium.WebElement, to: org.openqa.selenium.WebElement) {
        driver.executeJavaScript<Any>("$dragAndDropJs; dragAndDrop(arguments[0], arguments[1])", from, to)
    }

}

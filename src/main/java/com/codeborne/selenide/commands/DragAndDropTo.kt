package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Condition
import com.codeborne.selenide.DragAndDropOptions
import com.codeborne.selenide.DragAndDropOptions.Companion.usingJavaScript
import com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod
import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.Arguments
import com.codeborne.selenide.impl.ElementFinder
import com.codeborne.selenide.impl.FileContent
import com.codeborne.selenide.impl.WebElementSource
import com.codeborne.selenide.impl.WebElementWrapper
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class DragAndDropTo : Command<SelenideElement> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>?): SelenideElement {
        val target = findTarget(locator.driver(), args)
        target.shouldBe(Condition.visible)
        val options = Arguments(args)
            .ofType(DragAndDropOptions::class.java)
            .orElse(usingJavaScript())
        dragAndDrop(locator, target, options)
        return proxy
    }

    protected fun findTarget(driver: Driver, args: Array<out Any?>?): SelenideElement {
        return if (args == null || args.isEmpty()) {
            throw IllegalArgumentException("Missing target argument")
        } else if (args[0] is String) {
            ElementFinder.wrap(driver, By.cssSelector(args[0] as String))
        } else if (args[0] is WebElement) {
            WebElementWrapper.wrap(driver, args[0] as WebElement)
        } else {
            throw IllegalArgumentException(
                "Unknown target type: " + args[0] +
                        " (only String or WebElement are supported)"
            )
        }
    }

    private fun dragAndDrop(locator: WebElementSource, target: SelenideElement, options: DragAndDropOptions) {
        when (options.method) {
            DragAndDropMethod.JS -> dragAndDropUsingJavaScript(
                locator.driver(),
                locator.webElement,
                target.wrappedElement
            )
            DragAndDropMethod.ACTIONS -> dragAndDropUsingActions(
                locator.driver(),
                locator.webElement,
                target.wrappedElement
            )
            else -> throw IllegalArgumentException("Drag and Drop method not defined!")
        }
    }

    private fun dragAndDropUsingActions(driver: Driver, from: WebElement, target: WebElement) {
        Actions(driver.webDriver).dragAndDrop(from, target).perform()
    }

    private fun dragAndDropUsingJavaScript(driver: Driver, from: WebElement, to: WebElement) {
        driver.executeJavaScript<Any>(js.content + "; dragAndDrop(arguments[0], arguments[1])", from, to)
    }

    companion object {
        private val js = FileContent("drag_and_drop_script.js")
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement

class SelenideElementDescriber : ElementDescriber {
    override suspend fun fully(driver: Driver, element: org.openqa.selenium.WebElement?): String {
        return try {
            if (element == null) {
                "null"
            } else Describe(driver, element)
                .appendAttributes()
                .isSelected(element)
                .isDisplayed(element)
                .serialize()
        } catch (elementDoesNotExist: org.openqa.selenium.WebDriverException) {
            failedToDescribe(Cleanup.of.webdriverExceptionMessage(elementDoesNotExist))
        } catch (e: RuntimeException) {
            failedToDescribe(e.toString())
        }
    }
    override suspend fun briefly(driver: Driver, element: org.openqa.selenium.WebElement): String {
        return try {
            if (element == null) {
                return "null"
            }
            if (element is SelenideElement) {
                briefly(driver, element.toWebElement())
            } else Describe(driver, element).attr("id").attr("name").flush()
        } catch (elementDoesNotExist: org.openqa.selenium.WebDriverException) {
            failedToDescribe(Cleanup.of.webdriverExceptionMessage(elementDoesNotExist))
        } catch (e: RuntimeException) {
            failedToDescribe(e.toString())
        }
    }

    private fun failedToDescribe(s2: String): String {
        return "Ups, failed to described the element [caused by: $s2]"
    }
    override fun selector(selector: org.openqa.selenium.By?): String {
        return selector.toString()
            .replace("By.selector: ", "")
            .replace("By.cssSelector: ", "")
    }
}

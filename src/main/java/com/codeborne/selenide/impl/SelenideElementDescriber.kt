package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue

class SelenideElementDescriber : ElementDescriber {
    @CheckReturnValue
    override fun fully(driver: Driver, element: WebElement?): String {
        return try {
            if (element == null) {
                "null"
            } else Describe(driver, element)
                .appendAttributes()
                .isSelected(element)
                .isDisplayed(element)
                .serialize()
        } catch (elementDoesNotExist: WebDriverException) {
            failedToDescribe(Cleanup.of.webdriverExceptionMessage(elementDoesNotExist))
        } catch (e: RuntimeException) {
            failedToDescribe(e.toString())
        }
    }

    @CheckReturnValue
    override fun briefly(driver: Driver, element: WebElement): String {
        return try {
            if (element == null) {
                return "null"
            }
            if (element is SelenideElement) {
                briefly(driver, element.toWebElement())
            } else Describe(driver, element).attr("id").attr("name").flush()
        } catch (elementDoesNotExist: WebDriverException) {
            failedToDescribe(Cleanup.of.webdriverExceptionMessage(elementDoesNotExist))
        } catch (e: RuntimeException) {
            failedToDescribe(e.toString())
        }
    }

    private fun failedToDescribe(s2: String): String {
        return "Ups, failed to described the element [caused by: $s2]"
    }

    @CheckReturnValue
    override fun selector(selector: By?): String {
        return selector.toString()
            .replace("By.selector: ", "")
            .replace("By.cssSelector: ", "")
    }
}

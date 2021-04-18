package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Arrays
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Events internal constructor(private val log: Logger) {
    fun fireEvent(driver: Driver, element: WebElement, vararg event: String?) {
        try {
            executeJavaScript(driver, element, *event)
        } catch (ignore: StaleElementReferenceException) {
        } catch (e: Exception) {
            log.warn("Failed to trigger events {}: {}", listOf(*event), Cleanup.of.webdriverExceptionMessage(e))
        }
    }

    fun executeJavaScript(driver: Driver, element: WebElement, vararg event: String?) {
        driver.executeJavaScript<Any>(JS_CODE_TO_TRIGGER_EVENT, element, event)
    }

    companion object {
        var events = Events(
            LoggerFactory.getLogger(
                Events::class.java
            )
        )
        private const val JS_CODE_TO_TRIGGER_EVENT = "var webElement = arguments[0];\n" +
                "var eventNames = arguments[1];\n" +
                "for (var i = 0; i < eventNames.length; i++) {" +
                "  if (document.createEventObject) {\n" +  // IE
                "    var evt = document.createEventObject();\n" +
                "    webElement.fireEvent('on' + eventNames[i], evt);\n" +
                "  }\n" +
                "  else {\n" +
                "    var evt = document.createEvent('HTMLEvents');\n " +
                "    evt.initEvent(eventNames[i], true, true );\n " +
                "    webElement.dispatchEvent(evt);\n" +
                "  }\n" +
                '}'
    }
}

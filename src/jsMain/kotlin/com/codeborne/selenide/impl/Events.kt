package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.lighthousegames.logging.KmLog
import org.lighthousegames.logging.logging
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement

class Events internal constructor(private val log: KmLog) {
    fun fireEvent(driver: Driver, element: WebElement, vararg event: String?) {
        try {
            executeJavaScript(driver, element, *event)
        } catch (ignore: StaleElementReferenceException) {
        } catch (e: Exception) {
            log.warn { "Failed to trigger events ${listOf(*event)}: ${Cleanup.of.webdriverExceptionMessage(e)}" }
        }
    }

    fun executeJavaScript(driver: Driver, element: WebElement, vararg event: String?) {
        driver.executeJavaScript<Any>(JS_CODE_TO_TRIGGER_EVENT, element, event)
    }

    companion object {
        var events = Events(
            logging(
                Events::class
                    .simpleName
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

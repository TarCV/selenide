package org.openqa.selenium.support.events

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.events.WebDriverEventListener

actual class EventFiringWebDriver actual constructor(wrappedDriver: org.openqa.selenium.WebDriver):
    org.openqa.selenium.WebDriver {
    actual fun register(listener: WebDriverEventListener): Unit = TODO("Not yet implemented")
    actual override val currentUrl: String
        get() = TODO("Not yet implemented")
    actual override val pageSource: String
        get() = TODO("Not yet implemented")
    actual override val title: String
        get() = TODO("Not yet implemented")
    actual override val windowHandle: String
        get() = TODO("Not yet implemented")
    actual override val windowHandles: Set<String>
        get() = TODO("Not yet implemented")

    override actual fun manage(): org.openqa.selenium.WebDriver.Manager {
        TODO("Not yet implemented")
    }

    override actual fun navigate(): org.openqa.selenium.WebDriver.Navigator {
        TODO("Not yet implemented")
    }

    override actual fun switchTo(): org.openqa.selenium.WebDriver.TargetLocator {
        TODO("Not yet implemented")
    }

    override actual fun quit() {
        TODO("Not yet implemented")
    }

    override actual fun close() {
        TODO("Not yet implemented")
    }

    override actual suspend fun findElement(by: org.openqa.selenium.By): org.openqa.selenium.WebElement {
        TODO("Not yet implemented")
    }

    override actual suspend fun findElements(by: org.openqa.selenium.By): List<org.openqa.selenium.WebElement> {
        TODO("Not yet implemented")
    }
}

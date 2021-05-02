package org.openqa.selenium.support.events

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class EventFiringWebDriver(wrappedDriver: WebDriver): WebDriver {
    fun register(listener: WebDriverEventListener): Unit = TODO("Not yet implemented")
    override val currentUrl: String
        get() = TODO("Not yet implemented")
    override val pageSource: String
        get() = TODO("Not yet implemented")
    override val title: String
        get() = TODO("Not yet implemented")
    override val windowHandle: String
        get() = TODO("Not yet implemented")
    override val windowHandles: Set<String>
        get() = TODO("Not yet implemented")

    override fun manage(): WebDriver.Manager {
        TODO("Not yet implemented")
    }

    override fun navigate(): WebDriver.Navigator {
        TODO("Not yet implemented")
    }

    override fun switchTo(): WebDriver.TargetLocator {
        TODO("Not yet implemented")
    }

    override fun quit() {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun findElement(by: By): WebElement {
        TODO("Not yet implemented")
    }

    override fun findElements(by: By): List<WebElement> {
        TODO("Not yet implemented")
    }
}

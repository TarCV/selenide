package org.openqa.selenium.remote

import org.openqa.selenium.By
import org.openqa.selenium.Capabilities
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import support.net.URL

actual class RemoteWebDriver actual constructor(url: URL, capabilities: org.openqa.selenium.Capabilities):
    org.openqa.selenium.WebDriver {
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

    actual override fun manage(): org.openqa.selenium.WebDriver.Manager {
        TODO("Not yet implemented")
    }

    actual override fun navigate(): org.openqa.selenium.WebDriver.Navigator {
        TODO("Not yet implemented")
    }

    actual override fun switchTo(): org.openqa.selenium.WebDriver.TargetLocator {
        TODO("Not yet implemented")
    }

    actual override fun quit() {
        TODO("Not yet implemented")
    }

    actual override fun close() {
        TODO("Not yet implemented")
    }

    actual override suspend fun findElement(by: org.openqa.selenium.By): org.openqa.selenium.WebElement {
        TODO("Not yet implemented")
    }

    actual override suspend fun findElements(by: org.openqa.selenium.By): List<org.openqa.selenium.WebElement> {
        TODO("Not yet implemented")
    }
}

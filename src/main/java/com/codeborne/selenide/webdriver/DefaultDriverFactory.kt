package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.WebDriverProvider
import org.openqa.selenium.Capabilities
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import java.lang.reflect.InvocationTargetException
import javax.annotation.CheckReturnValue

class DefaultDriverFactory : AbstractDriverFactory() {
    override fun setupWebdriverBinary() {}
    @CheckReturnValue
    override fun create(config: Config?, browser: Browser, proxy: Proxy?, browserDownloadsFolder: File?): WebDriver {
        return createInstanceOf(config!!.browser(), config, browser, proxy, browserDownloadsFolder)
    }

    @CheckReturnValue
    private fun createInstanceOf(
        className: String, config: Config?, browser: Browser,
        proxy: Proxy?, browserDownloadsFolder: File?
    ): WebDriver {
        val clazz = classOf(config!!.browser())
        return if (WebDriverProvider::class.java.isAssignableFrom(clazz)) {
            val capabilities: Capabilities =
                createCapabilities(config, browser, proxy, browserDownloadsFolder)
            createInstanceOf(WebDriverProvider::class.java, clazz).createDriver(
                DesiredCapabilities(
                    capabilities
                )
            )
        } else if (DriverFactory::class.java.isAssignableFrom(clazz)) {
            val factory = createInstanceOf(DriverFactory::class.java, clazz)
            if (config.driverManagerEnabled()) {
                factory.setupWebdriverBinary()
            }
            factory.create(config, browser, proxy, browserDownloadsFolder)
        } else {
            val capabilities: Capabilities =
                createCapabilities(config, browser, proxy, browserDownloadsFolder)
            createWebDriver(className, capabilities)
        }
    }

    @CheckReturnValue
    override fun createCapabilities(
      config: Config?, browser: Browser,
      proxy: Proxy?, browserDownloadsFolder: File?
    ): MutableCapabilities {
        val clazz = classOf(config!!.browser())
        if (DriverFactory::class.java.isAssignableFrom(clazz)) {
            val factory = createInstanceOf(DriverFactory::class.java, clazz)
            return factory.createCapabilities(config, browser, proxy, browserDownloadsFolder)
        }
        return createCommonCapabilities(config, browser, proxy)
    }

    private fun classOf(className: String): Class<*> {
        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("Class not found: $className", e)
        }
    }

    private fun createWebDriver(className: String, capabilities: Capabilities): WebDriver {
        return try {
            val constructor = Class.forName(className).getConstructor(Capabilities::class.java)
            constructor.newInstance(capabilities) as WebDriver
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to create WebDriver of type $className", e)
        }
    }

    private fun <T> createInstanceOf(resultClass: Class<T>, clazz: Class<*>): T {
        return try {
            val constructor = clazz.getDeclaredConstructor()
            constructor.isAccessible = true
            constructor.newInstance() as T
        } catch (e: InvocationTargetException) {
            throw runtime(e.targetException)
        } catch (invalidClassName: Exception) {
            throw IllegalArgumentException(invalidClassName)
        }
    }

    private fun runtime(exception: Throwable): RuntimeException {
        return if (exception is RuntimeException) exception else RuntimeException(exception)
    }
}

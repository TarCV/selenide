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

class DefaultDriverFactory : AbstractDriverFactory() {
    override fun setupWebdriverBinary() {}
    override fun create(config: Config, browser: Browser, proxy: Proxy?, browserDownloadsFolder: File?): WebDriver {
        return createInstanceOf(config.browser(), config, browser, proxy, browserDownloadsFolder)
    }
    private fun createInstanceOf(
        className: String, config: Config, browser: Browser,
        proxy: Proxy?, browserDownloadsFolder: File?
    ): WebDriver {
        val clazz = config.browser()::class
        return if (WebDriverProvider::class.isInstance(clazz)) {
            val capabilities: Capabilities =
                createCapabilities(config, browser, proxy, browserDownloadsFolder)
            createInstanceOf(WebDriverProvider::class, clazz).createDriver(
                DesiredCapabilities(
                    capabilities
                )
            )
        } else if (DriverFactory::class.isInstance(clazz)) {
            val factory = createInstanceOf(DriverFactory::class, clazz)
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
    override fun createCapabilities(
      config: Config, browser: Browser,
      proxy: Proxy?, browserDownloadsFolder: File?
    ): MutableCapabilities {
        val clazz = config.browser()::class
        if (DriverFactory::class.isInstance(clazz)) {
            val factory = createInstanceOf(DriverFactory::class, clazz)
            return factory.createCapabilities(config, browser, proxy, browserDownloadsFolder)
        }
        return createCommonCapabilities(config, browser, proxy)
    }

/* TODO:
    private fun classOf(className: String): kotlin.reflect.KClass<*> {
        return try {
            kotlin.reflect.KClass.forName(className)
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("Class not found: $className", e)
        }
    }


    private fun createWebDriver(className: String, capabilities: Capabilities): WebDriver {
        return try {
            val constructor = kotlin.reflect.KClass.forName(className).getConstructor(Capabilities::class)
            constructor.newInstance(capabilities) as WebDriver
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to create WebDriver of type $className", e)
        }
    }
*/
    private fun <T: Any> createInstanceOf(resultClass: kotlin.reflect.KClass<T>, clazz: kotlin.reflect.KClass<*>): T {
        return try {
            val constructor = clazz.getDeclaredConstructor()
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

package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.impl.FileHelper
import okio.ExperimentalFileSystem
import okio.Path
import okio.Path.Companion.toPath
import org.lighthousegames.logging.logging
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import support.System

abstract class AbstractDriverFactory : DriverFactory {
    // This was used in java version:
    // TODO: private val fileNamer = FileNamer()
    @ExperimentalFileSystem
    protected fun webdriverLog(config: Config): Path {
        val logFolder = FileHelper.ensureFolderExists(FileHelper.canonicalPath(config.reportsFolder().toPath()))
        val logFileName = "webdriver.${"log" /*fileNamer.generateFileName()*/}.log"
        val logFile = FileHelper.canonicalPath(logFolder / logFileName)
        log.info { "Write webdriver logs to: $logFile" }
        return logFile
    }

    /* TODO: protected fun <DS : DriverService?, B : DriverService.Builder<DS, *>> withLog(config: Config, dsBuilder: B): DS {
        if (config.webdriverLogsEnabled()) {
            dsBuilder.withLogFile(webdriverLog(config))
        }
        return dsBuilder.build()
    }*/
    fun createCommonCapabilities(config: Config, browser: Browser, proxy: /* TODO: Proxy*/Any?): org.openqa.selenium.MutableCapabilities {
        val capabilities = org.openqa.selenium.remote.DesiredCapabilities()
/* TODO:
        if (proxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, proxy)
        }
*/
        config.browserVersion()?.let {
            if (it.isNotEmpty()) {
                capabilities.version = it
            }
        }
        capabilities.setCapability(org.openqa.selenium.remote.CapabilityType.PAGE_LOAD_STRATEGY, config.pageLoadStrategy())
        capabilities.setCapability(org.openqa.selenium.remote.CapabilityType.ACCEPT_SSL_CERTS, true)
        if (browser.supportsInsecureCerts()) {
            capabilities.setCapability(org.openqa.selenium.remote.CapabilityType.ACCEPT_INSECURE_CERTS, true)
        }
        capabilities.isJavascriptEnabled = true
        capabilities.setCapability(org.openqa.selenium.remote.CapabilityType.TAKES_SCREENSHOT, true)
        capabilities.setCapability(org.openqa.selenium.remote.CapabilityType.SUPPORTS_ALERTS, true)
// TODO:        transferCapabilitiesFromSystemProperties(capabilities)
        return MergeableCapabilities(capabilities, config.browserCapabilities())
    }

    /*protected fun transferCapabilitiesFromSystemProperties(currentBrowserCapabilities: DesiredCapabilities) {
        val prefix = "capabilities."
        for (key in System.getProperties().stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                val capability = key.substring(prefix.length)
                val value = System.getProperties().getProperty(key)
                log.debug("Use ${}=${}", key, value)
                currentBrowserCapabilities.setCapability(capability, convertStringToNearestObjectType(value))
            }
        }
    }*/

    /**
     * Converts String to Boolean\Integer or returns original String.
     * @param value string to convert
     * @return string's object representation
     */
    fun convertStringToNearestObjectType(value: String): Any {
        return when {
            isBoolean(value) -> value.toBoolean()
            isInteger(value) -> value.toInt()
            else -> value
        }
    }
    protected fun isInteger(value: String): Boolean {
        return REGEX_SIGNED_INTEGER.matches(value)
    }
    protected fun isBoolean(value: String?): Boolean {
        return "true".equals(value, ignoreCase = true) || "false".equals(value, ignoreCase = true)
    }
    protected fun isSystemPropertyNotSet(key: String): Boolean {
        return System.getProperty(key, "").isBlank()
    }
    fun majorVersion(browserVersion: String?): Int {
        if (browserVersion == null) return 0
        if (browserVersion.isNullOrBlank()) return 0
        return if (REGEX_VERSION.matches(browserVersion)) REGEX_VERSION.replaceFirst(browserVersion, "$1").toInt() else 0
    }

    protected fun <T> cast(value: Any): T {
        return value as T
    }

    companion object {
        private val log = logging(AbstractDriverFactory::class.simpleName)
        private val REGEX_SIGNED_INTEGER = kotlin.text.Regex("^-?\\d+$")
        private val REGEX_VERSION = kotlin.text.Regex("(\\d+)(\\..*)?")
    }
}

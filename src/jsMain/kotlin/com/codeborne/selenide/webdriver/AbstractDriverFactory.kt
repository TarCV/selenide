package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.impl.FileHelper
import com.codeborne.selenide.impl.FileNamer
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.service.DriverService
import org.slf4j.LoggerFactory
import java.io.File
import support.System

abstract class AbstractDriverFactory : DriverFactory {
    private val fileNamer = FileNamer()
    protected fun webdriverLog(config: Config): File {
        val logFolder = FileHelper.ensureFolderExists(File(config.reportsFolder()).absoluteFile)
        val logFileName = "webdriver.${fileNamer.generateFileName()}.log"
        val logFile = File(logFolder, logFileName).absoluteFile
        log.info("Write webdriver logs to: {}", logFile)
        return logFile
    }

    /* TODO: protected fun <DS : DriverService?, B : DriverService.Builder<DS, *>> withLog(config: Config, dsBuilder: B): DS {
        if (config.webdriverLogsEnabled()) {
            dsBuilder.withLogFile(webdriverLog(config))
        }
        return dsBuilder.build()
    }*/
    /*fun createCommonCapabilities(config: Config, browser: Browser, proxy: Proxy?): MutableCapabilities {
        val capabilities = DesiredCapabilities()
        if (proxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, proxy)
        }
        if (config.browserVersion() != null && config.browserVersion()?.isEmpty() != true) {
            capabilities.version = config.browserVersion()
        }
        capabilities.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, config.pageLoadStrategy())
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
        if (browser.supportsInsecureCerts()) {
            capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true)
        }
        capabilities.isJavascriptEnabled = true
        capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true)
        capabilities.setCapability(CapabilityType.SUPPORTS_ALERTS, true)
        transferCapabilitiesFromSystemProperties(capabilities)
        return MergeableCapabilities(capabilities, config.browserCapabilities())
    }

    protected fun transferCapabilitiesFromSystemProperties(currentBrowserCapabilities: DesiredCapabilities) {
        val prefix = "capabilities."
        for (key in System.getProperties().stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                val capability = key.substring(prefix.length)
                val value = System.getProperties().getProperty(key)
                log.debug("Use {}={}", key, value)
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
        return if (isBoolean(value)) {
            Boolean.valueOf(value)
        } else if (isInteger(value)) {
            value.toInt()
        } else {
            value
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
        private val log = LoggerFactory.getLogger(AbstractDriverFactory::class)
        private val REGEX_SIGNED_INTEGER = kotlin.text.Regex("^-?\\d+$")
        private val REGEX_VERSION = kotlin.text.Regex("(\\d+)(\\..*)?")
    }
}

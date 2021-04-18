package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import com.codeborne.selenide.impl.FileHelper
import com.codeborne.selenide.impl.FileNamer
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.service.DriverService
import org.slf4j.LoggerFactory
import java.io.File
import java.util.regex.Pattern
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
abstract class AbstractDriverFactory : DriverFactory {
    private val fileNamer = FileNamer()
    @CheckReturnValue
    protected fun webdriverLog(config: Config): File {
        val logFolder = FileHelper.ensureFolderExists(File(config.reportsFolder()).absoluteFile)
        val logFileName = String.format("webdriver.%s.log", fileNamer.generateFileName())
        val logFile = File(logFolder, logFileName).absoluteFile
        log.info("Write webdriver logs to: {}", logFile)
        return logFile
    }

    protected fun <DS : DriverService?, B : DriverService.Builder<DS, *>> withLog(config: Config, dsBuilder: B): DS {
        if (config.webdriverLogsEnabled()) {
            dsBuilder.withLogFile(webdriverLog(config))
        }
        return dsBuilder.build()
    }

    @CheckReturnValue
    fun createCommonCapabilities(config: Config, browser: Browser, proxy: Proxy?): MutableCapabilities {
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
    }

    /**
     * Converts String to Boolean\Integer or returns original String.
     * @param value string to convert
     * @return string's object representation
     */
    @CheckReturnValue
    fun convertStringToNearestObjectType(value: String): Any {
        return if (isBoolean(value)) {
            java.lang.Boolean.valueOf(value)
        } else if (isInteger(value)) {
            value.toInt()
        } else {
            value
        }
    }

    @CheckReturnValue
    protected fun isInteger(value: String): Boolean {
        return REGEX_SIGNED_INTEGER.matcher(value).matches()
    }

    @CheckReturnValue
    protected fun isBoolean(value: String?): Boolean {
        return "true".equals(value, ignoreCase = true) || "false".equals(value, ignoreCase = true)
    }

    @CheckReturnValue
    protected fun isSystemPropertyNotSet(key: String): Boolean {
        return StringUtils.isBlank(System.getProperty(key, ""))
    }

    @CheckReturnValue
    fun majorVersion(browserVersion: String?): Int {
        if (browserVersion == null) return 0
        if (StringUtils.isBlank(browserVersion)) return 0
        val matcher = REGEX_VERSION.matcher(browserVersion)
        return if (matcher.matches()) matcher.replaceFirst("$1").toInt() else 0
    }

    protected fun <T> cast(value: Any): T {
        return value as T
    }

    companion object {
        private val log = LoggerFactory.getLogger(AbstractDriverFactory::class.java)
        private val REGEX_SIGNED_INTEGER = Pattern.compile("^-?\\d+$")
        private val REGEX_VERSION = Pattern.compile("(\\d+)(\\..*)?")
    }
}

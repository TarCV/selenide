package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Browser
import com.codeborne.selenide.Config
import io.github.bonigarcia.wdm.WebDriverManager
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.Capabilities
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.LoggerFactory
import java.io.File
import java.util.Arrays
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors
import javax.annotation.CheckReturnValue

open class ChromeDriverFactory : AbstractDriverFactory() {
    override fun setupWebdriverBinary() {
        if (isSystemPropertyNotSet("webdriver.chrome.driver")) {
            WebDriverManager.chromedriver().setup()
        }
    }

    @CheckReturnValue
    override fun create(config: Config?, browser: Browser, proxy: Proxy?, browserDownloadsFolder: File?): WebDriver {
        val chromeOptions = createCapabilities(config, browser, proxy, browserDownloadsFolder)
        log.debug("Chrome options: {}", chromeOptions)
        return ChromeDriver(buildService(config), chromeOptions)
    }

    @CheckReturnValue
    protected open fun buildService(config: Config?): ChromeDriverService {
        return withLog(config!!, ChromeDriverService.Builder())
    }

    @CheckReturnValue
    override fun createCapabilities(
        config: Config?, browser: Browser,
        proxy: Proxy?, browserDownloadsFolder: File?
    ): MutableCapabilities {
        val commonCapabilities: Capabilities = createCommonCapabilities(config!!, browser, proxy)
        val options = ChromeOptions()
        options.setHeadless(config.headless())
        if (!config.browserBinary()!!.isEmpty()) {
            log.info("Using browser binary: {}", config.browserBinary())
            options.setBinary(config.browserBinary())
        }
        options.addArguments(createChromeArguments(config, browser))
        options.setExperimentalOption("excludeSwitches", excludeSwitches(commonCapabilities))
        options.setExperimentalOption("prefs", prefs(browserDownloadsFolder))
        setMobileEmulation(options)
        return MergeableCapabilities(options, commonCapabilities)
    }

    @CheckReturnValue
    protected open fun createChromeArguments(config: Config, browser: Browser?): List<String> {
        val arguments: MutableList<String> = ArrayList()
        arguments.add("--proxy-bypass-list=<-loopback>")
        arguments.add("--disable-dev-shm-usage")
        arguments.add("--no-sandbox")
        arguments.addAll(parseArguments(System.getProperty("chromeoptions.args")))
        arguments.addAll(createHeadlessArguments(config))
        return arguments
    }

    @CheckReturnValue
    protected fun createHeadlessArguments(config: Config): List<String> {
        val arguments: MutableList<String> = ArrayList()
        if (config.headless()) {
            arguments.add("--disable-background-networking")
            arguments.add("--enable-features=NetworkService,NetworkServiceInProcess")
            arguments.add("--disable-background-timer-throttling")
            arguments.add("--disable-backgrounding-occluded-windows")
            arguments.add("--disable-breakpad")
            arguments.add("--disable-client-side-phishing-detection")
            arguments.add("--disable-component-extensions-with-background-pages")
            arguments.add("--disable-default-apps")
            arguments.add("--disable-features=TranslateUI")
            arguments.add("--disable-hang-monitor")
            arguments.add("--disable-ipc-flooding-protection")
            arguments.add("--disable-popup-blocking")
            arguments.add("--disable-prompt-on-repost")
            arguments.add("--disable-renderer-backgrounding")
            arguments.add("--disable-sync")
            arguments.add("--force-color-profile=srgb")
            arguments.add("--metrics-recording-only")
            arguments.add("--no-first-run")
            arguments.add("--password-store=basic")
            arguments.add("--use-mock-keychain")
            arguments.add("--hide-scrollbars")
            arguments.add("--mute-audio")
        }
        return arguments
    }

    @CheckReturnValue
    protected open fun excludeSwitches(capabilities: Capabilities): Array<String> {
        return if (hasExtensions(capabilities)) arrayOf("enable-automation") else arrayOf(
            "enable-automation",
            "load-extension"
        )
    }

    private fun hasExtensions(capabilities: Capabilities): Boolean {
        val chromeOptions = capabilities.getCapability("goog:chromeOptions") as Map<*, *>?
            ?: return false
        val extensions = chromeOptions["extensions"] as List<*>?
        return extensions != null && !extensions.isEmpty()
    }

    private fun setMobileEmulation(chromeOptions: ChromeOptions) {
        val mobileEmulation = mobileEmulation()
        if (!mobileEmulation.isEmpty()) {
            chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation)
        }
    }

    @CheckReturnValue
    protected fun mobileEmulation(): Map<String, Any> {
        val mobileEmulation = System.getProperty("chromeoptions.mobileEmulation", "")
        return parsePreferencesFromString(mobileEmulation)
    }

    @CheckReturnValue
    protected fun prefs(browserDownloadsFolder: File?): Map<String, Any> {
        val chromePreferences: MutableMap<String, Any> = HashMap()
        chromePreferences["credentials_enable_service"] = false
        chromePreferences["plugins.always_open_pdf_externally"] = true
        chromePreferences["profile.default_content_setting_values.automatic_downloads"] = 1
        if (browserDownloadsFolder != null) {
            chromePreferences["download.default_directory"] = browserDownloadsFolder.absolutePath
        }
        chromePreferences.putAll(parsePreferencesFromString(System.getProperty("chromeoptions.prefs", "")))
        log.debug("Using chrome preferences: {}", chromePreferences)
        return chromePreferences
    }

    @CheckReturnValue
    private fun parsePreferencesFromString(preferencesString: String): Map<String, Any> {
        val prefs: MutableMap<String, Any> = HashMap()
        val allPrefs = parseCSV(preferencesString)
        for (pref in allPrefs) {
            val keyValue = removeQuotes(pref).split("=").toTypedArray()
            if (keyValue.size == 1) {
                log.warn(
                    "Missing '=' sign while parsing <key=value> pairs from {}. Key '{}' is ignored.",
                    preferencesString, keyValue[0]
                )
                continue
            } else if (keyValue.size > 2) {
                log.warn(
                    "More than one '=' sign while parsing <key=value> pairs from {}. Key '{}' is ignored.",
                    preferencesString, keyValue[0]
                )
                continue
            }
            val prefValue = convertStringToNearestObjectType(keyValue[1])
            prefs[keyValue[0]] = prefValue
        }
        return prefs
    }

    @CheckReturnValue
    private fun parseArguments(arguments: String?): List<String> {
        return parseCSV(arguments).stream()
            .map { value: String -> removeQuotes(value) }
            .collect(Collectors.toList())
    }

    @CheckReturnValue
    private fun removeQuotes(value: String): String {
        return REGEX_REMOVE_QUOTES.matcher(value).replaceAll(Matcher.quoteReplacement(""))
    }

    /**
     * parse parameters which can come from command-line interface
     * @param csvString comma-separated values, quotes can be used to mask spaces and commas
     * Example: 123,"foo bar","bar,foo"
     * @return values as array, quotes are preserved
     */
    @CheckReturnValue
    fun parseCSV(csvString: String?): List<String> {
        return if (StringUtils.isBlank(csvString)) emptyList() else Arrays.asList(
            *REGEX_COMMAS_IN_VALUES.split(
                csvString
            )
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(ChromeDriverFactory::class.java)

        // Regexp from https://stackoverflow.com/a/15739087/1110503 to handle commas in values
        private val REGEX_COMMAS_IN_VALUES = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")
        private val REGEX_REMOVE_QUOTES = Pattern.compile("\"", Pattern.LITERAL)
    }
}

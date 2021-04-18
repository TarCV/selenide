package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.AuthenticationType
import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.FileDownloadMode
import com.codeborne.selenide.SelenideDriver
import com.codeborne.selenide.logevents.SelenideLogger
import org.openqa.selenium.WebDriverException
import support.URL

class Navigator {
    private val basicAuthUrl = BasicAuthUrl()
    suspend fun open(driver: SelenideDriver, relativeOrAbsoluteUrl: String) {
        navigateTo(driver, relativeOrAbsoluteUrl, AuthenticationType.BASIC, "", "", "")
    }

    suspend fun open(driver: SelenideDriver, url: URL) {
        navigateTo(driver, url.toExternalForm(), AuthenticationType.BASIC, "", "", "")
    }

    suspend fun open(driver: SelenideDriver, relativeOrAbsoluteUrl: String, domain: String, login: String, password: String) {
        navigateTo(driver, relativeOrAbsoluteUrl, AuthenticationType.BASIC, domain, login, password)
    }

    suspend fun open(driver: SelenideDriver, url: URL, domain: String, login: String, password: String) {
        navigateTo(driver, url.toExternalForm(), AuthenticationType.BASIC, domain, login, password)
    }

/*
TODO:
    fun open(
        driver: SelenideDriver, relativeOrAbsoluteUrl: String,
        authenticationType: AuthenticationType, credentials: Credentials
    ) {
        navigateTo(driver, relativeOrAbsoluteUrl, authenticationType, "", credentials.login, credentials.password)
    }
*/

/* TODO:
    private fun basicAuthRequestFilter(selenideProxy: SelenideProxyServer): AuthenticationFilter? {
        return selenideProxy.requestFilter("authentication")
    }
*/

    fun absoluteUrl(config: Config, relativeOrAbsoluteUrl: String): String {
        return if (isAbsoluteUrl(relativeOrAbsoluteUrl)) relativeOrAbsoluteUrl else config.baseUrl() + relativeOrAbsoluteUrl
    }

    private suspend fun navigateTo(
        driver: SelenideDriver, relativeOrAbsoluteUrl: String,
        authenticationType: AuthenticationType, domain: String, login: String, password: String
    ) {
        checkThatProxyIsEnabled(driver.config())
        val absoluteUrl = absoluteUrl(driver.config(), relativeOrAbsoluteUrl)
        val url = appendBasicAuthIfNeeded(driver.config(), absoluteUrl, authenticationType, domain, login, password)
        SelenideLogger.runAsync("open", url) {
            try {
                val webDriver = driver.andCheckWebDriver
                beforeNavigateTo(driver.config(), null/*driver.proxy!!*/, authenticationType, domain, login, password)
                webDriver.navigate().to(url)
            } catch (e: WebDriverException) {
                e.addInfo("selenide.url", url)
                e.addInfo("selenide.baseUrl", driver.config().baseUrl() ?: "")
                val configRemote = driver.config().remote()
                if (configRemote != null) {
                    e.addInfo("selenide.remote", configRemote)
                }
                throw e
            }
        }
    }

    fun open(driver: SelenideDriver) {
        checkThatProxyIsEnabled(driver.config())
        SelenideLogger.run("open", "") { driver.andCheckWebDriver }
    }

    private fun checkThatProxyIsEnabled(config: Config) {
        check(!(!config.proxyEnabled() && config.fileDownload() === FileDownloadMode.PROXY)) {
            "config.proxyEnabled == false but config.fileDownload == PROXY. " +
                    "You need to enable proxy server automatically."
        }
    }

/* TODO:
    private fun checkThatProxyIsStarted(selenideProxy: SelenideProxyServer) {
        checkNotNull(selenideProxy) {
            "config.proxyEnabled == true but proxy server is not created. " +
                    "You need to call `setWebDriver(webDriver, selenideProxy)` instead of `setWebDriver(webDriver)` if you need to use proxy."
        }
        check(selenideProxy.isStarted) { "config.proxyEnabled == true but proxy server is not started." }
    }
*/

    private fun beforeNavigateTo(
        config: Config, selenideProxy: /*SelenideProxyServer*/Nothing?,
        authenticationType: AuthenticationType, domain: String, login: String, password: String
    ) {
        /* TODO: if (config.proxyEnabled()) {
            checkThatProxyIsStarted(selenideProxy)
            beforeNavigateToWithProxy(selenideProxy, authenticationType, domain, login, password)
        } else*/run {
            beforeNavigateToWithoutProxy(authenticationType, domain, login, password)
        }
    }

/* TODO:    private fun beforeNavigateToWithProxy(
        selenideProxy: Nothing? *//* TODO: SelenideProxyServer*//*,
        authenticationType: AuthenticationType, domain: String, login: String, password: String
    ) {
        if (hasAuthentication(domain, login, password)) {
            basicAuthRequestFilter(selenideProxy)!!.setAuthentication(authenticationType, Credentials(login, password))
        } else {
            basicAuthRequestFilter(selenideProxy)!!.removeAuthentication()
        }
    }*/

    private fun beforeNavigateToWithoutProxy(
        authenticationType: AuthenticationType,
        domain: String,
        login: String,
        password: String
    ) {
        if (hasAuthentication(domain, login, password) && authenticationType != AuthenticationType.BASIC) {
            throw UnsupportedOperationException("Cannot use $authenticationType authentication without proxy server")
        }
    }

    private fun hasAuthentication(domain: String, login: String, password: String): Boolean {
        return !domain.isEmpty() || !login.isEmpty() || !password.isEmpty()
    }

    private fun appendBasicAuthIfNeeded(
        config: Config, url: String,
        authType: AuthenticationType, domain: String, login: String, password: String
    ): String {
        return if (passBasicAuthThroughUrl(
                config,
                authType,
                domain,
                login,
                password
            )
        ) basicAuthUrl.appendBasicAuthToURL(url, domain, login, password) else url
    }

    private fun passBasicAuthThroughUrl(
        config: Config,
        authenticationType: AuthenticationType, domain: String, login: String, password: String
    ): Boolean {
        return hasAuthentication(domain, login, password) &&
                !config.proxyEnabled() && authenticationType == AuthenticationType.BASIC
    }

    fun isAbsoluteUrl(relativeOrAbsoluteUrl: String): Boolean {
        return ABSOLUTE_URL_REGEX.matcher(relativeOrAbsoluteUrl).matches()
    }

    fun back(driver: Driver) {
        SelenideLogger.run("back", "") { driver.webDriver.navigate().back() }
    }

    fun forward(driver: Driver) {
        SelenideLogger.run("forward", "") { driver.webDriver.navigate().forward() }
    }

    fun refresh(driver: Driver) {
        SelenideLogger.run("refresh", "") { driver.webDriver.navigate().refresh() }
    }

    companion object {
        private val ABSOLUTE_URL_REGEX = kotlin.text.Regex("^[a-zA-Z]+:.*", kotlin.text.Regex.MULTILINE)
    }
}

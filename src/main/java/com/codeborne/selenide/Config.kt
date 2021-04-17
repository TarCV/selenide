package com.codeborne.selenide

import org.openqa.selenium.MutableCapabilities

interface Config {
    fun browser(): String
    fun headless(): Boolean
    fun remote(): String?
    fun browserSize(): String?
    fun browserVersion(): String?
    fun browserPosition(): String?
    fun startMaximized(): Boolean
    fun driverManagerEnabled(): Boolean
    fun webdriverLogsEnabled(): Boolean
    fun browserBinary(): String?
    fun pageLoadStrategy(): String?
    fun pageLoadTimeout(): Long
    fun browserCapabilities(): MutableCapabilities?
    fun baseUrl(): String?
    fun timeout(): Long
    fun pollingInterval(): Long
    fun holdBrowserOpen(): Boolean
    fun reopenBrowserOnFail(): Boolean
    fun clickViaJs(): Boolean
    fun screenshots(): Boolean
    fun savePageSource(): Boolean
    fun reportsFolder(): String
    fun downloadsFolder(): String
    fun reportsUrl(): String?
    fun fastSetValue(): Boolean
    fun versatileSetValue(): Boolean
    fun selectorMode(): SelectorMode?
    fun assertionMode(): AssertionMode?
    fun fileDownload(): FileDownloadMode
    fun proxyEnabled(): Boolean
    fun proxyHost(): String
    fun proxyPort(): Int
}

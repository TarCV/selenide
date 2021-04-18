package com.codeborne.selenide.impl

import com.codeborne.selenide.files.FileFilter
import com.codeborne.selenide.proxy.FileDownloadFilter
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.function.Predicate
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class DownloadFileWithProxyServer internal constructor(
    private val waiter: Waiter,
    private val windowsCloser: WindowsCloser
) {
    constructor() : this(Waiter(), WindowsCloser()) {}

    @CheckReturnValue
    @Throws(FileNotFoundException::class)
    fun download(
        anyClickableElement: WebElementSource,
        clickable: WebElement, timeout: Long,
        fileFilter: FileFilter
    ): File {
        val webDriver = anyClickableElement.driver().webDriver
        return windowsCloser.runAndCloseArisedWindows(
            webDriver
        ) { clickAndInterceptFileByProxyServer(anyClickableElement, clickable, timeout, fileFilter) }
    }

    @Throws(FileNotFoundException::class)
    private fun clickAndInterceptFileByProxyServer(
        anyClickableElement: WebElementSource, clickable: WebElement,
        timeout: Long, fileFilter: FileFilter
    ): File {
        val config = anyClickableElement.driver().config()
        check(config.proxyEnabled()) { "Cannot download file: proxy server is not enabled. Setup proxyEnabled" }
        val proxyServer = anyClickableElement.driver().proxy
            ?: throw IllegalStateException("Cannot download file: proxy server is not started")
        val filter = proxyServer.responseFilter<FileDownloadFilter>("download")
            ?: throw IllegalStateException("Cannot download file: download filter is not activated")
        filter.activate()
        return try {
            waiter.wait(filter, PreviousDownloadsCompleted(), timeout, config.pollingInterval())
            filter.reset()
            clickable.click()
            waiter.wait(
                filter,
                HasDownloads(fileFilter),
                timeout,
                config.pollingInterval()
            )
            if (log.isInfoEnabled) {
                log.info(filter.downloads().filesAsString())
                log.info(
                    "Just in case, all intercepted responses: {}",
                    filter.responsesAsString()
                )
            }
            filter.downloads().firstDownloadedFile(anyClickableElement.toString(), timeout, fileFilter)
        } finally {
            filter.deactivate()
        }
    }

    @ParametersAreNonnullByDefault
    private class HasDownloads internal constructor(private val fileFilter: FileFilter) : Predicate<FileDownloadFilter> {
        override fun test(filter: FileDownloadFilter): Boolean {
            return !filter.downloads().files(fileFilter).isEmpty()
        }
    }

    @ParametersAreNonnullByDefault
    private class PreviousDownloadsCompleted : Predicate<FileDownloadFilter> {
        private var downloadsCount = -1
        override fun test(filter: FileDownloadFilter): Boolean {
            return try {
                downloadsCount == filter.downloads().size()
            } finally {
                downloadsCount = filter.downloads().size()
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DownloadFileWithProxyServer::class.java)
    }
}

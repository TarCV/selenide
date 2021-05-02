package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadOptions
import com.codeborne.selenide.DownloadOptions.Companion.using
import com.codeborne.selenide.FileDownloadMode
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.files.FileFilter
import com.codeborne.selenide.files.FileFilters
import com.codeborne.selenide.impl.DownloadFileToFolder
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.WebElementSource
import okio.ExperimentalFileSystem
import okio.Path
import org.lighthousegames.logging.logging

@ExperimentalFileSystem
class DownloadFile internal constructor(
    private val downloadFileWithHttpRequest: /*DownloadFileWithHttpRequest*/Nothing?,
    private val downloadFileWithProxyServer: /*DownloadFileWithProxyServer*/Nothing?,
    private val downloadFileToFolder: DownloadFileToFolder
) : Command<Path> {
    constructor() : this(
        null, null, Plugins.injectA<DownloadFileToFolder>(
            DownloadFileToFolder::class
        )
    ) {
    }
    override suspend fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): Path {
        val link = locator.findAndAssertElementIsInteractable()
        val config = locator.driver().config()
        val options = getDownloadOptions(config, args)
        val timeout = options.getTimeout(config.timeout())
        log.debug { "Download file: $options" }
        return when (options.method) {
/* TODO:
            FileDownloadMode.HTTPGET -> {
                downloadFileWithHttpRequest.download(locator.driver(), link, timeout, options.filter)
            }
            FileDownloadMode.PROXY -> {
                downloadFileWithProxyServer.download(locator, link, timeout, options.filter)
            }
*/
            FileDownloadMode.FOLDER -> {
                downloadFileToFolder.download(locator, link, timeout, options.filter)
            }
            else -> {
                throw IllegalArgumentException("Unknown file download mode: " + options.method)
            }
        }
    }
    private fun getDownloadOptions(config: Config, args: Array<out Any?>?): DownloadOptions {
        return if (args != null && args.isNotEmpty() && args[0] is DownloadOptions) {
            args[0] as DownloadOptions
        } else using(
            config.fileDownload()
        )
            .withFilter(getFileFilter(args))
            .withTimeout(getTimeout(config, args))
    }
    fun getTimeout(config: Config, args: Array<out Any?>?): Long {
        return if (args != null && args.isNotEmpty() && args[0] is Long) {
            args[0] as Long
        } else {
            config.timeout()
        }
    }
    fun getFileFilter(args: Array<out Any?>?): FileFilter {
        if (args != null && args.isNotEmpty() && args[0] is FileFilter) {
            return args[0] as FileFilter
        }
        return if (args != null && args.size > 1 && args[1] is FileFilter) {
            args[1] as FileFilter
        } else {
            FileFilters.none()
        }
    }

    companion object {
        private val log = logging(DownloadFile::class.simpleName)
    }
}

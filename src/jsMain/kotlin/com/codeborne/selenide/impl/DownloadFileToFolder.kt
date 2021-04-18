package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.files.DownloadedFile
import com.codeborne.selenide.files.FileFilter
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory
import java.io.File
import okio.okio.FileNotFoundException
import support.System

class DownloadFileToFolder internal constructor(
    private val downloader: Downloader,
    private val waiter: Waiter,
    private val windowsCloser: WindowsCloser
) {
    constructor() : this(Downloader(), Waiter(), WindowsCloser()) {}
    suspend fun download(
        anyClickableElement: WebElementSource,
        clickable: WebElement, timeout: Long,
        fileFilter: FileFilter
    ): File {
        val webDriver = anyClickableElement.driver().webDriver
        return windowsCloser.runAndCloseArisedWindows(
            webDriver
        ) { clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable, timeout, fileFilter) }
    }
    private suspend fun clickAndWaitForNewFilesInDownloadsFolder(
        anyClickableElement: WebElementSource,
        clickable: WebElement,
        timeout: Long,
        fileFilter: FileFilter
    ): File {
        val driver = anyClickableElement.driver()
        val config = driver.config()
        val folder =
            driver.browserDownloadsFolder() ?: throw IllegalStateException("Downloads folder is not configured")
        folder.cleanupBeforeDownload()
        val downloadStartedAt = System.currentTimeMillis()
        clickable.click()
        val newDownloads = waitForNewFiles(timeout, fileFilter, config, folder, downloadStartedAt)
        val downloadedFile = newDownloads.firstDownloadedFile(anyClickableElement.toString(), timeout, fileFilter)
        return archiveFile(config, downloadedFile)
    }

    private suspend fun waitForNewFiles(
      timeout: Long, fileFilter: FileFilter, config: Config,
      folder: DownloadsFolder, clickMoment: Long
    ): Downloads {
        val hasDownloads = HasDownloads(fileFilter, clickMoment)
        waiter.wait(folder, hasDownloads, timeout, config.pollingInterval())
        if (log.isInfoEnabled) {
          log.info(hasDownloads.downloads.filesAsString())
        }
        if (log.isDebugEnabled) {
          log.debug("All downloaded files in {}: {}", folder, folder.files())
        }
        return hasDownloads.downloads
    }

    private fun archiveFile(config: Config, downloadedFile: File): File {
        val uniqueFolder = downloader.prepareTargetFolder(config)
        val archivedFile = File(uniqueFolder, downloadedFile.name).absoluteFile
        FileHelper.moveFile(downloadedFile, archivedFile)
        return archivedFile
    }

        private class HasDownloads(private val fileFilter: FileFilter, private val downloadStartedAt: Long) : (DownloadsFolder) -> Boolean {
        lateinit var downloads: Downloads
        override operator fun invoke(folder: DownloadsFolder): Boolean {
            Downloads(newFiles(folder)).let {
              downloads = it
              return it.files(fileFilter).isNotEmpty()
            }
        }

        private fun newFiles(folder: DownloadsFolder): List<DownloadedFile> {
            return folder.files()
                .filter { obj: File -> obj.isFile }
                .filter { file: File -> isFileModifiedLaterThan(file, downloadStartedAt) }
                .map { file: File -> DownloadedFile(file, emptyMap()) }

        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DownloadFileToFolder::class)

        /**
         * Depending on OS, file modification time can have seconds precision, not milliseconds.
         * We have to ignore the difference in milliseconds.
         */
        fun isFileModifiedLaterThan(file: File, timestamp: Long): Boolean {
            return file.lastModified() >= timestamp / 1000L * 1000L
        }
    }
}

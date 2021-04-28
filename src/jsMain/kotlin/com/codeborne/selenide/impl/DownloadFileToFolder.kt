package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.files.DownloadedFile
import com.codeborne.selenide.files.FileFilter
import okio.ExperimentalFileSystem
import okio.Path
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory
import support.System

class DownloadFileToFolder internal constructor(
    private val downloader: Downloader,
    private val waiter: Waiter,
    private val windowsCloser: WindowsCloser
) {
    constructor() : this(Downloader(), Waiter(), WindowsCloser())

    @ExperimentalFileSystem
    suspend fun download(
        anyClickableElement: WebElementSource,
        clickable: WebElement, timeout: Long,
        fileFilter: FileFilter
    ): Path {
        val webDriver = anyClickableElement.driver().webDriver
        return windowsCloser.runAndCloseArisedWindows(
            webDriver
        ) { clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable, timeout, fileFilter) }
    }
    @ExperimentalFileSystem
    private suspend fun clickAndWaitForNewFilesInDownloadsFolder(
        anyClickableElement: WebElementSource,
        clickable: WebElement,
        timeout: Long,
        fileFilter: FileFilter
    ): Path {
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

    @ExperimentalFileSystem
    private suspend fun waitForNewFiles(
      timeout: Long, fileFilter: FileFilter, config: Config,
      folder: DownloadsFolder, clickMoment: Long
    ): Downloads {
        val hasDownloads = HasDownloads(fileFilter, clickMoment)
        waiter.wait(folder, { hasDownloads(it) }, timeout, config.pollingInterval())
        if (log.isInfoEnabled) {
          log.info(hasDownloads.downloads.filesAsString())
        }
        if (log.isDebugEnabled) {
          log.debug("All downloaded files in {}: {}", folder, folder.files())
        }
        return hasDownloads.downloads
    }

    @ExperimentalFileSystem
    private fun archiveFile(config: Config, downloadedFile: Path): Path {
        val uniqueFolder = downloader.prepareTargetFolder(config)
        val archivedFile = FileHelper.canonicalPath(uniqueFolder / downloadedFile.name)
        FileHelper.moveFile(downloadedFile, archivedFile)
        return archivedFile
    }

    @ExperimentalFileSystem
    private class HasDownloads(private val fileFilter: FileFilter, private val downloadStartedAt: Long) {
        lateinit var downloads: Downloads
        operator fun invoke(folder: DownloadsFolder): Boolean {
            Downloads(newFiles(folder)).let {
              downloads = it
              return it.files(fileFilter).isNotEmpty()
            }
        }

        @ExperimentalFileSystem
        private fun newFiles(folder: DownloadsFolder): List<DownloadedFile> {
            return folder.files()
                .filter { obj: Path -> FileHelper.isFile(obj) }
                .filter { file: Path -> isFileModifiedLaterThan(file, downloadStartedAt) }
                .map { file: Path -> DownloadedFile(file, emptyMap()) }

        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DownloadFileToFolder::class)

        /**
         * Depending on OS, file modification time can have seconds precision, not milliseconds.
         * We have to ignore the difference in milliseconds.
         */
        @ExperimentalFileSystem
        fun isFileModifiedLaterThan(file: Path, timestamp: Long): Boolean {
            return FileHelper.lastModified(file) >= timestamp / 1000L * 1000L
        }
    }
}

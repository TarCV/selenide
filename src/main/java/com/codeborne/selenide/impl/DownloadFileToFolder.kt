package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import com.codeborne.selenide.DownloadsFolder
import com.codeborne.selenide.files.DownloadedFile
import com.codeborne.selenide.files.FileFilter
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.function.Predicate
import java.util.stream.Collectors
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class DownloadFileToFolder internal constructor(
    private val downloader: Downloader,
    private val waiter: Waiter,
    private val windowsCloser: WindowsCloser
) {
    constructor() : this(Downloader(), Waiter(), WindowsCloser()) {}

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
        ) { clickAndWaitForNewFilesInDownloadsFolder(anyClickableElement, clickable, timeout, fileFilter) }
    }

    @CheckReturnValue
    @Throws(FileNotFoundException::class)
    private fun clickAndWaitForNewFilesInDownloadsFolder(
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

    private fun waitForNewFiles(
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

    @ParametersAreNonnullByDefault
    private class HasDownloads(private val fileFilter: FileFilter, private val downloadStartedAt: Long) :
        Predicate<DownloadsFolder> {
        lateinit var downloads: Downloads
        override fun test(folder: DownloadsFolder): Boolean {
            Downloads(newFiles(folder)).let {
              downloads = it
              return it.files(fileFilter).isNotEmpty()
            }
        }

        private fun newFiles(folder: DownloadsFolder): List<DownloadedFile> {
            return folder.files().stream()
                .filter { obj: File -> obj.isFile }
                .filter { file: File -> isFileModifiedLaterThan(file, downloadStartedAt) }
                .map { file: File -> DownloadedFile(file, emptyMap()) }
                .collect(Collectors.toList())
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DownloadFileToFolder::class.java)

        /**
         * Depending on OS, file modification time can have seconds precision, not milliseconds.
         * We have to ignore the difference in milliseconds.
         */
        @JvmStatic
        fun isFileModifiedLaterThan(file: File, timestamp: Long): Boolean {
            return file.lastModified() >= timestamp / 1000L * 1000L
        }
    }
}

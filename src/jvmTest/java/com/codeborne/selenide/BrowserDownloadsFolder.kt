package com.codeborne.selenide

import com.codeborne.selenide.impl.FileHelper
import okio.ExperimentalFileSystem
import okio.Path
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import javax.annotation.ParametersAreNonnullByDefault

/**
 * A unique folder per browser.
 * It effectively means that Selenide can delete all files in this folder before starting every new download.
 */
@ExperimentalFileSystem
@ParametersAreNonnullByDefault
class BrowserDownloadsFolder private constructor(folder: Path) : DownloadsFolder(folder) {
    override fun cleanupBeforeDownload() {
        try {
            if (FileHelper.exists(folder)) {
                FileUtils.cleanDirectory(folder.toFile())
            }
        } catch (e: IOException) {
            throw IllegalStateException("Failed to cleanup folder " + folder.toFile().absolutePath, e)
        }
    }

    companion object {
        @JvmStatic
        fun from(folder: Path): BrowserDownloadsFolder {
            return BrowserDownloadsFolder(folder)
        }
    }
}

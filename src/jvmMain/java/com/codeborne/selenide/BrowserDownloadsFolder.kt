package com.codeborne.selenide

import org.apache.commons.io.FileUtils
import java.io.IOException
import javax.annotation.ParametersAreNonnullByDefault

/**
 * A unique folder per browser.
 * It effectively means that Selenide can delete all files in this folder before starting every new download.
 */
@ParametersAreNonnullByDefault
class BrowserDownloadsFolder private constructor(folder: Path) : DownloadsFolder(folder) {
    override fun cleanupBeforeDownload() {
        try {
            if (folder.exists()) {
                FileUtils.cleanDirectory(folder)
            }
        } catch (e: IOException) {
            throw IllegalStateException("Failed to cleanup folder " + folder.absolutePath, e)
        }
    }

    companion object {
        @JvmStatic
        fun from(folder: Path?): BrowserDownloadsFolder? {
            return folder?.let { BrowserDownloadsFolder(it) }
        }
    }
}

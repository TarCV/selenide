package com.codeborne.selenide.impl

import com.codeborne.selenide.files.DownloadedFile
import okio.ExperimentalFileSystem

/**
 * Sort all downloaded files by "likeness" to be the right download.
 *
 *
 * 1. Response with "Content-Disposition" is most likely the right download.
 * 2. Response with type "text/html", "text/plain", "text/css", "text/javascript", "application/json"
 * are less likely the right download.
 * 3. Latest file wins
 * 4. The first file (alphabetically) wins
 *
 */
@ExperimentalFileSystem
class DownloadDetector : Comparator<DownloadedFile> {
    override fun compare(file1: DownloadedFile, file2: DownloadedFile): Int {
        var result = file2.hasContentDispositionHeader() == file1.hasContentDispositionHeader()
        if (result) {
            val isHtmlOrCss1 = LOW_RANK_CONTENT_TYPES.contains(file1.contentType)
            val isHtmlOrCss2 = LOW_RANK_CONTENT_TYPES.contains(file2.contentType)
            result = isHtmlOrCss1 == isHtmlOrCss2
            if (result) {
                result = file1.file.lastModified() == file2.file.lastModified()
                if (result) {
                    result = file1.file.name.compareTo(file2.file.name)
                }
            }
        }
        return result
    }

    companion object {
        private val LOW_RANK_CONTENT_TYPES: Set<String?> = HashSet(
            listOf(
                "text/html", "text/plain", "text/css", "text/javascript", "application/json"
            )
        )
    }
}

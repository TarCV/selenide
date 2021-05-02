package com.codeborne.selenide.impl

import co.touchlab.stately.collections.IsoMutableList
import com.codeborne.selenide.files.DownloadedFile
import com.codeborne.selenide.files.FileFilter
import okio.ExperimentalFileSystem
import okio.FileNotFoundException
import okio.Path

@ExperimentalFileSystem
class Downloads {
    private val files: MutableList<DownloadedFile> = IsoMutableList()

    constructor()
    constructor(files: List<DownloadedFile>) {
        this.files.addAll(files)
    }

    fun clear() {
        files.clear()
    }

    fun add(file: DownloadedFile) {
        files.add(file)
    }
    fun files(): List<DownloadedFile> {
        return files
    }
    fun files(fileFilter: FileFilter): List<DownloadedFile> {
        return files.filter { file: DownloadedFile -> fileFilter.match(file) }
    }
    fun firstMatchingFile(fileFilter: FileFilter): DownloadedFile? {
        return files
            .filter { file: DownloadedFile -> fileFilter.match(file) }
            .sortedWith(DownloadDetector())
            .firstOrNull()
    }
    fun filesAsString(): String {
        val sb = StringBuilder()
        sb.append("Downloaded ").append(files.size).append(" files:\n")
        for ((i, file) in files.withIndex()) {
            sb.append("  #").append(i + 1).append("  ").append(FileHelper.canonicalPath(file.file)).appendLine()
        }
        return sb.toString()
    }

    fun size(): Int {
        return files.size
    }
    fun firstDownloadedFile(context: String, timeout: Long, fileFilter: FileFilter): Path {
        if (size() == 0) {
            throw FileNotFoundException("Failed to download file $context in $timeout ms.")
        }
        return firstMatchingFile(fileFilter)?.file
            ?: throw FileNotFoundException(
                    "Failed to download file $context in $timeout ms.${fileFilter.description()}"
                )
    }
}

package com.codeborne.selenide.impl

import com.codeborne.selenide.files.DownloadedFile
import com.codeborne.selenide.files.FileFilter
import java.io.File
import okio.okio.FileNotFoundException

class Downloads {
    private val files: MutableList<DownloadedFile> = CopyOnWriteArrayList()

    constructor() {}
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
        return files.filter { file: DownloadedFile -> fileFilter.match(file) }.sorted(DownloadDetector())
            .findFirst()
    }
    fun filesAsString(): String {
        val sb = StringBuilder()
        sb.append("Downloaded ").append(files.size).append(" files:\n")
        for ((i, file) in files.withIndex()) {
            sb.append("  #").append(i + 1).append("  ").append(file.file.absolutePath).appendLine()
        }
        return sb.toString()
    }

    fun size(): Int {
        return files.size
    }
    fun firstDownloadedFile(context: String, timeout: Long, fileFilter: FileFilter): File {
        if (size() == 0) {
            throw FileNotFoundException("Failed to download file $context in $timeout ms.")
        }
        return firstMatchingFile(fileFilter)
            .orElseThrow {
                FileNotFoundException(
                    "Failed to download file $context in $timeout ms.${fileFilter.description()}"
                )
            }.file
    }
}
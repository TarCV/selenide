package com.codeborne.selenide.impl

import com.codeborne.selenide.files.DownloadedFile
import com.codeborne.selenide.files.FileFilter
import java.io.File
import java.io.FileNotFoundException
import java.util.Optional
import java.util.concurrent.CopyOnWriteArrayList
import java.util.stream.Collectors
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
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

    @CheckReturnValue
    fun files(): List<DownloadedFile> {
        return files
    }

    @CheckReturnValue
    fun files(fileFilter: FileFilter): List<DownloadedFile> {
        return files.stream().filter { file: DownloadedFile -> fileFilter.match(file) }.collect(Collectors.toList())
    }

    @CheckReturnValue
    fun firstMatchingFile(fileFilter: FileFilter): Optional<DownloadedFile> {
        return files.stream().filter { file: DownloadedFile -> fileFilter.match(file) }.sorted(DownloadDetector())
            .findFirst()
    }

    @CheckReturnValue
    fun filesAsString(): String {
        val sb = StringBuilder()
        sb.append("Downloaded ").append(files.size).append(" files:\n")
        for ((i, file) in files.withIndex()) {
            sb.append("  #").append(i + 1).append("  ").append(file.file.absolutePath).append("\n")
        }
        return sb.toString()
    }

    fun size(): Int {
        return files.size
    }

    @CheckReturnValue
    @Throws(FileNotFoundException::class)
    fun firstDownloadedFile(context: String, timeout: Long, fileFilter: FileFilter): File {
        if (size() == 0) {
            throw FileNotFoundException("Failed to download file $context in $timeout ms.")
        }
        return firstMatchingFile(fileFilter)
            .orElseThrow {
                FileNotFoundException(
                    String.format(
                        "Failed to download file %s in %d ms.%s",
                        context, timeout, fileFilter.description()
                    )
                )
            }.file
    }
}

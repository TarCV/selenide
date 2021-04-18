package com.codeborne.selenide

import java.io.File

abstract class DownloadsFolder protected constructor(protected val folder: File) {
    fun toFile(): File {
        return folder
    }
    fun files(): List<File> {
        val files = folder.listFiles()
        return if (files == null) emptyList() else listOf(*files)
    }

    abstract fun cleanupBeforeDownload()
    fun file(fileName: String): File {
        return File(folder, fileName).absoluteFile
    }

    override fun toString(): String {
        return folder.absolutePath
    }
}

package com.codeborne.selenide

import com.codeborne.selenide.impl.FileHelper
import okio.ExperimentalFileSystem
import okio.Path

@ExperimentalFileSystem
abstract class DownloadsFolder protected constructor(protected val folder: Path) {
    fun toFile(): Path {
        return folder
    }
    fun files(): List<Path> {
        return FileHelper.listFiles(folder)
    }

    abstract fun cleanupBeforeDownload()
    fun file(fileName: String): Path {
        return (folder / fileName)
    }

    override fun toString(): String {
        return folder.toString()
    }
}

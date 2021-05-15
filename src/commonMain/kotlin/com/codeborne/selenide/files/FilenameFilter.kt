package com.codeborne.selenide.files

import okio.ExperimentalFileSystem

class FilenameFilter(private val fileName: String) : FileFilter {
    @ExperimentalFileSystem
    override fun match(file: DownloadedFile): Boolean {
        return file.file.name == fileName
    }

    override fun description(): String {
        return "with file name \"$fileName\""
    }

    override fun toString(): String {
        return description()
    }
}

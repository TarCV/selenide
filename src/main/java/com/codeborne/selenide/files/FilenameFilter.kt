package com.codeborne.selenide.files

internal class FilenameFilter(private val fileName: String) : FileFilter {
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

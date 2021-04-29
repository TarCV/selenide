package com.codeborne.selenide.files

import okio.ExperimentalFileSystem

internal class ExtensionFilter(private val extension: String) : FileFilter {
    @ExperimentalFileSystem
    override fun match(file: DownloadedFile): Boolean {
        return file.file.name.endsWith(extension)
    }

    override fun description(): String {
        return "with extension \"$extension\""
    }

    override fun toString(): String {
        return description()
    }
}

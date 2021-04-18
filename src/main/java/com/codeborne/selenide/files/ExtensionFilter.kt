package com.codeborne.selenide.files

import org.apache.commons.io.FilenameUtils

internal class ExtensionFilter(private val extension: String) : FileFilter {
    override fun match(file: DownloadedFile): Boolean {
        return FilenameUtils.isExtension(file.file.name, extension)
    }

    override fun description(): String {
        return "with extension \"$extension\""
    }

    override fun toString(): String {
        return description()
    }
}

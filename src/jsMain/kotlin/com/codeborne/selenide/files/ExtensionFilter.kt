package com.codeborne.selenide.files

internal class ExtensionFilter(private val extension: String) : FileFilter {
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

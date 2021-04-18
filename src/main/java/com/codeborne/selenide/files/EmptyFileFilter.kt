package com.codeborne.selenide.files

internal class EmptyFileFilter : FileFilter {
    override fun match(file: DownloadedFile): Boolean {
        return true
    }

    override fun description(): String {
        return ""
    }

    override val isEmpty: Boolean = true

    override fun toString(): String {
        return description()
    }
}

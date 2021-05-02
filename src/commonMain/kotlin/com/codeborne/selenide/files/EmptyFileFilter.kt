package com.codeborne.selenide.files

import okio.ExperimentalFileSystem

class EmptyFileFilter : FileFilter {
    @ExperimentalFileSystem
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

package com.codeborne.selenide.files

import okio.ExperimentalFileSystem

interface FileFilter {
    @ExperimentalFileSystem
    fun match(file: DownloadedFile): Boolean
    fun description(): String
    val isEmpty: Boolean
        get() = false
}

package com.codeborne.selenide.files

interface FileFilter {
    fun match(file: DownloadedFile): Boolean
    fun description(): String
    val isEmpty: Boolean
        get() = false
}

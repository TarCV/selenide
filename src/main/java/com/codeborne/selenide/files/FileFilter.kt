package com.codeborne.selenide.files

import java.io.Serializable

interface FileFilter : Serializable {
    fun match(file: DownloadedFile): Boolean
    fun description(): String
    val isEmpty: Boolean
        get() = false
}
